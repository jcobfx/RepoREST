package pl.com.foks.repoinfo.github;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import pl.com.foks.repoinfo.exceptions.BranchesNotFoundException;
import pl.com.foks.repoinfo.exceptions.RepositoriesNotFoundException;
import pl.com.foks.repoinfo.github.model.GitHubBranch;
import pl.com.foks.repoinfo.github.model.GitHubRepo;

import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class GitHubService {
    private final RestClient gitHubRestClient;

    public GitHubService(RestClient gitHubRestClient) {
        this.gitHubRestClient = gitHubRestClient;
    }

    public boolean userExists(String username) {
        AtomicBoolean userExists = new AtomicBoolean(true);
        gitHubRestClient.get()
                .uri("/users/{username}", username)
                .retrieve()
                .onStatus(status -> status.isSameCodeAs(HttpStatus.NOT_FOUND),
                        (request, response) -> userExists.set(false))
                .toBodilessEntity();
        return userExists.get();
    }

    public GitHubRepo[] getGitHubRepos(String username) {
        var repos = gitHubRestClient.get()
                .uri("/users/{username}/repos", username)
                .retrieve()
                .toEntity(GitHubRepo[].class);
        if (repos.getStatusCode() == HttpStatus.OK) {
            return repos.getBody();
        } else {
            throw new RepositoriesNotFoundException(HttpStatus.NOT_FOUND, "Failed to fetch repositories for user: " + username);
        }
    }

    public GitHubBranch[] getGitHubBranches(String username, String repoName) {
        var branches = gitHubRestClient.get()
                .uri("/repos/{username}/{repoName}/branches", username, repoName)
                .retrieve()
                .toEntity(GitHubBranch[].class);
        if (branches.getStatusCode() == HttpStatus.OK) {
            return branches.getBody();
        } else {
            throw new BranchesNotFoundException(HttpStatus.NOT_FOUND, "Failed to fetch branches for repository: " + repoName);
        }
    }
}
