package pl.com.foks.reporest;

import java.util.List;

public class GithubReposResponse {
    private GithubReposResponse() {
    }

    public record Repo(String name, String ownerLogin, List<Branch> branches) {
        public Repo(GithubRepo repo, List<GithubBranch> branches) {
            this(repo.name(), repo.owner().login(), branches.stream()
                    .map(Branch::from)
                    .toList());
        }
    }

    public record Branch(String name, String commitSha) {
        public static Branch from(GithubBranch branch) {
            return new Branch(branch.name(), branch.commit().sha());
        }
    }
}
