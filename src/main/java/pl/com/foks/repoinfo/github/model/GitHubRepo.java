package pl.com.foks.repoinfo.github.model;

public record GitHubRepo(String name, GitHubUser owner, boolean fork) {
}
