package pl.com.foks.reporest;

import java.util.List;

public record GithubReposResponse(List<GithubRepo> repos) {
}
