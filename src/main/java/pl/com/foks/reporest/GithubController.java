package pl.com.foks.reporest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GithubController {
    private final GithubService githubService;

    @GetMapping("/repos")
    public ResponseEntity<GithubReposResponse> getGithubRepos(@RequestParam String username) {
        GithubReposResponse response = githubService.getUserRepositoriesNotForksWithBranches(username);
        return ResponseEntity.ok(response);
    }
}
