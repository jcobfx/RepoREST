package pl.com.foks.repoinfo;

import pl.com.foks.repoinfo.model.GitHubBranch;
import pl.com.foks.repoinfo.model.GitHubRepo;

import java.util.List;

public class ReposResponseDTO {
    private ReposResponseDTO() {
    }

    public record RepoApiDTO(String name, String ownerLogin, List<BranchApiDTO> branches) {
        public RepoApiDTO(GitHubRepo repo, List<GitHubBranch> branches) {
            this(repo.name(), repo.owner().login(), branches.stream()
                    .map(BranchApiDTO::from)
                    .toList());
        }
    }

    public record BranchApiDTO(String name, String commitSha) {
        public static BranchApiDTO from(GitHubBranch branch) {
            return new BranchApiDTO(branch.name(), branch.commit().sha());
        }
    }
}
