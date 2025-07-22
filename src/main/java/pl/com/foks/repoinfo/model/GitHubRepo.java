package pl.com.foks.repoinfo.model;

public record GitHubRepo(String name, GitHubUser owner, boolean fork) {
}
