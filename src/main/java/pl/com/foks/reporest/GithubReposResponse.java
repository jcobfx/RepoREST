package pl.com.foks.reporest;

import lombok.Builder;

import java.util.List;

@Builder
public record GithubReposResponse(List<Repo> repositories) {

    @Builder
    public record Repo(String name, String ownerLogin, List<Branch> branches) {
    }

    @Builder
    public record Branch(String name, String commitSha) {
    }
}
