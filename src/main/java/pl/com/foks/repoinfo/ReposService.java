package pl.com.foks.repoinfo;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import pl.com.foks.repoinfo.exceptions.BranchesNotFoundException;
import pl.com.foks.repoinfo.exceptions.RepositoriesNotFoundException;
import pl.com.foks.repoinfo.exceptions.UserNotFoundException;
import pl.com.foks.repoinfo.github.GitHubService;

import java.util.List;
import java.util.stream.Stream;

@Service
public class ReposService {
    private final GitHubService githubService;

    public ReposService(GitHubService githubService) {
        this.githubService = githubService;
    }

    public List<ReposResponseDTO.RepoApiDTO> getRepos(String username) {
        if (githubService.userExists(username)) {
            var gitHubRepos = githubService.getGitHubRepos(username);
            if (gitHubRepos == null) {
                throw new RepositoriesNotFoundException(HttpStatus.NOT_FOUND,
                        "Repositories are null for user: " + username);

            }
            return Stream.of(gitHubRepos)
                    .map(repo -> {
                        var gitHubBranches = githubService.getGitHubBranches(username, repo.name());
                        if (gitHubBranches == null) {
                            throw new BranchesNotFoundException(HttpStatus.NOT_FOUND,
                                    "Branches are null for repository: " + repo.name());
                        }

                        return new ReposResponseDTO.RepoApiDTO(repo, List.of(gitHubBranches));
                    })
                    .toList();
        } else {
            throw new UserNotFoundException(HttpStatus.NOT_FOUND, "User not found: " + username);
        }
    }
}
