package pl.com.foks.repoinfo;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ReposRestController {
    private final ReposService reposService;

    public ReposRestController(ReposService reposService) {
        this.reposService = reposService;
    }

    @GetMapping("/repos")
    public ResponseEntity<List<ReposResponseDTO.RepoApiDTO>> getRepos(@RequestParam String username) {
        var response = reposService.getRepos(username);
        return ResponseEntity.ok(response);
    }
}
