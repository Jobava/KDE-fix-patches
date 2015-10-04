package fixpatches;

public final class Repo {
    private final String repoName;
    private final String repoURL;

    public Repo(String name, String URL) {
        this.repoName = name;
        this.repoURL = URL;
    }

    public String getRepoName() {
        return repoName;
    }

    public String getRepoURL() {
        return repoURL;
    }
}
