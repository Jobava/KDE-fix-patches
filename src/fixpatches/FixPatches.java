package fixpatches;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author Jobava
 */
public class FixPatches {

    private static List<Repo> readReposCSV(String fileName) throws IOException {
        File file = new File(fileName);
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        String line;
        List<Repo> results = new ArrayList<>();
        while ((line = br.readLine()) != null) {
            if (line.contains("svn://")) {
                Repo current = new Repo(line.split(",")[0], line.split(",")[1]);
                results.add(current);
            }
        }
        br.close();
        fr.close();
        return results;
    }

    /**
     * Create a folder called 'fixed-patches' containing patches with fixed
     * coordinates
     *
     * @param repos
     */
    private static void fixPatches(List<Repo> repoList) throws IOException {
        Map<String, String> changeMap = new HashMap<>();
        for (Repo r : repoList) {
            changeMap.put(r.getRepoName(), r.getRepoURL().split("/kde/")[1]);
        }
        Util.safeDeleteRecreateFolder("patches-fixed");

        List<String> fileNames = new ArrayList<>();
        File folder = new File("patches/");
        File[] patches = folder.listFiles();
        Arrays.sort(patches);
        for (File file: patches) {
            if (file.isFile()) {
                if (file.getName().endsWith(".patch")) {
                    fileNames.add(file.getName());
                }
            }
        }
        
        //read all .patch files in the folder 'patches'
        //and change the file locations to conform with the svn structure
        //of the KDE project
        for (String ff : fileNames) {
            File f = new File("patches/" + ff);
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);
            String line;
            int lineNumber = 0;
            StringBuilder buffer = new StringBuilder();
            while ((line = br.readLine()) != null) {
                ++lineNumber;
                for (String s : changeMap.keySet()) {
                    if (line.contains(s)) {
                        if (line.startsWith("diff --")
                                || line.startsWith("--- ")
                                || line.startsWith("+++ ")
                                || line.startsWith(" " + s)) {
                            line = line.replace(s, changeMap.get(s));
                            break;
                        }
                    }
                }
                buffer.append(line);
                buffer.append("\n");
            }
            br.close();
            fr.close();

            File outFile = new File ("patches-fixed/" + ff);
            outFile.getParentFile().mkdirs();
            Files.write(Paths.get("patches-fixed/" + ff), buffer.toString().getBytes());
        }
    }

    /**
     * Create a folder called 'patches' in the current folder and put there the
     * files found at the URL designated by listOfURLs, one URL per line
     *
     * @param listOfURLs the name of the file
     * @throws IOException
     */
    private static void getPatches(String listOfURLs) throws FileNotFoundException, IOException {
        File file = new File(listOfURLs);
        if (!file.exists()) {
            throw new RuntimeException("The file " + listOfURLs + " does not exist!");
        }
        if (!file.canRead()) {
            throw new RuntimeException("The file " + listOfURLs + " cannot be read!");
        }

        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        String line;

        Util.safeDeleteRecreateFolder("patches");

        int count = 1;
        while ((line = br.readLine()) != null) {
            if (line.startsWith("https://")) {
                String patchName = "patches/" + Integer.toString(count) + ".patch";
                URL url = new URL(line.trim());
                FileUtils.copyURLToFile(url, new File(patchName));
                ++count;
            }
        }
        br.close();
        fr.close();
    }

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        getPatches("patches.txt");
        fixPatches(readReposCSV("repos.csv"));
    }

}
