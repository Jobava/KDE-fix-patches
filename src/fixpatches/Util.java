package fixpatches;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author Jobava
 */
public class Util {
    public static void safeDeleteRecreateFolder(String folderName) throws IOException {
        //Delete existing folder and create it again
        File f = new File(folderName);
        if (f.exists()) {
            if (!f.isDirectory()) {
                throw new RuntimeException("There is a file called " + folderName + ", please delete that!");
            }
            //the whole point of this up to now ↓↓↓↓
            FileUtils.deleteDirectory(f);

            if (f.exists()) {
                throw new RuntimeException("The folder " + folderName + " could not be deleted, please delete it manually!");
            }
           
            //now create it again
            if (! (new File(folderName).mkdir() )) {
                throw new RuntimeException("Could not recreate " + folderName + " folder after it's been deleted!");
            }
        }
    }
}
