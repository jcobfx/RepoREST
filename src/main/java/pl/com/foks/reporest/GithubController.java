package pl.com.foks.reporest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class GithubController {
    private final GithubService githubService;

    @GetMapping("/repos")
    public ResponseEntity<List<GithubReposResponse.Repo>> getGithubRepos(@RequestParam String username) {
        if (!githubService.userExists(username)) {
            throw new UserNotFoundException(HttpStatus.NOT_FOUND, "User not found: " + username);
        }

        var repos = githubService.getGithubRepos(username);
        if (repos.getStatusCode() != HttpStatus.OK || repos.getBody() == null) {
            throw new RepositoriesNotFoundException(HttpStatus.NOT_FOUND,
                    "Failed to fetch repositories.");
        }

        var response = Arrays.stream(repos.getBody())
                .filter(repo -> !repo.fork())
                .map(repo -> {
                    var branches = githubService.getGithubBranches(username, repo.name());
                    if (branches.getStatusCode() != HttpStatus.OK || branches.getBody() == null) {
                        throw new BranchesNotFoundException(HttpStatus.NOT_FOUND,
                                "Failed to fetch branches for the repository: " + repo.name());
                    }
                    return new GithubReposResponse.Repo(repo, Arrays.asList(branches.getBody()));
                })
                .toList();

        return ResponseEntity.ok(response);
    }
}
