package pl.com.foks.reporest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
public class GithubService {
    private final RestClient restClient;

    public GithubReposResponse getUserRepositoriesNotForksWithBranches(String username) {
        if (!userExists(username)) {
            throw new UserNotFoundException(HttpStatus.NOT_FOUND, "User not found: " + username);
        }

        ResponseEntity<GithubRepo[]> repos = getGithubRepos(username);
        if (repos.getStatusCode() != HttpStatus.OK || repos.getBody() == null) {
            throw new RepositoriesNotFoundException(HttpStatus.resolve(repos.getStatusCode().value()),
                    "Failed to fetch repositories for the user: " + username);
        }

        List<GithubReposResponse.Repo> repositories = Arrays.stream(repos.getBody())
                .filter(repo -> !repo.fork()) // Filter out forked repositories
                .map(repo -> {
                    ResponseEntity<GithubBranch[]> branches = getGithubBranches(username, repo.name());
                    if (branches.getStatusCode() != HttpStatus.OK || branches.getBody() == null) {
                        throw new BranchesNotFoundException(HttpStatus.resolve(branches.getStatusCode().value()),
                                "Failed to fetch branches for the repository: " + repo.name());
                    }

                    List<GithubReposResponse.Branch> branchList = Arrays.stream(branches.getBody())
                            .map(branch -> GithubReposResponse.Branch.builder()
                                    .name(branch.name())
                                    .commitSha(branch.commit().sha())
                                    .build())
                            .toList();

                    return GithubReposResponse.Repo.builder()
                            .name(repo.name())
                            .ownerLogin(repo.owner().login())
                            .branches(branchList)
                            .build();
                })
                .toList();

        return GithubReposResponse.builder()
                .repositories(repositories)
                .build();
    }

    boolean userExists(String username) {
        AtomicBoolean userExists = new AtomicBoolean(true);
        restClient.get()
                .uri("/users/{username}", username)
                .retrieve()
                .onStatus(status -> status.isSameCodeAs(HttpStatus.NOT_FOUND),
                        (request, response) -> userExists.set(false))
                .toBodilessEntity();
        return userExists.get();
    }

    ResponseEntity<GithubRepo[]> getGithubRepos(String username) {
        return restClient.get()
                .uri("/users/{username}/repos", username)
                .retrieve()
                .toEntity(GithubRepo[].class);
    }

    ResponseEntity<GithubBranch[]> getGithubBranches(String username, String repoName) {
        return restClient.get()
                .uri("/repos/{username}/{repoName}/branches", username, repoName)
                .retrieve()
                .toEntity(GithubBranch[].class);
    }
}
