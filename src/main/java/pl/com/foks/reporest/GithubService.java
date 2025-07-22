package pl.com.foks.reporest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
public class GithubService {
    private final RestClient restClient;

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
