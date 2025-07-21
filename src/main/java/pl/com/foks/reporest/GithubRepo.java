package pl.com.foks.reporest;

public record GithubRepo(String name, GithubUser owner, boolean fork) {
}
