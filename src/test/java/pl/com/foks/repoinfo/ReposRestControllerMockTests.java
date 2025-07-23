package pl.com.foks.repoinfo;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import pl.com.foks.repoinfo.github.GitHubService;
import pl.com.foks.repoinfo.github.model.GitHubBranch;
import pl.com.foks.repoinfo.github.model.GitHubRepo;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReposRestController.class)
@Import(ReposService.class)
public class ReposRestControllerMockTests {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GitHubService githubService;

    @MockitoSpyBean
    private ReposService reposService;

    private static GitHubRepo[] gitHubRepos;
    private static GitHubBranch[] gitHubBranches;

    @BeforeAll
    public static void setUp() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        gitHubRepos = objectMapper.readValue(ReposRestControllerMockTests.class.getResourceAsStream("/responses/github-repos.json"), GitHubRepo[].class);
        gitHubBranches = objectMapper.readValue(ReposRestControllerMockTests.class.getResourceAsStream("/responses/github-branches.json"), GitHubBranch[].class);

        assertThat(gitHubRepos).isNotNull();
        assertThat(gitHubBranches).isNotNull();
    }

    @Test
    public void should_ReturnRepositoryWithBranches_When_UserExists() throws Exception {
        // Given
        when(githubService.userExists("octocat")).thenReturn(true);
        when(githubService.getGitHubRepos("octocat")).thenReturn(gitHubRepos);
        when(githubService.getGitHubBranches("octocat", "Hello-World"))
                .thenReturn(gitHubBranches);

        // When
        var resultActions = this.mockMvc.perform(get("/repos?username=octocat"))
                .andDo(print());

        // Then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Hello-World")))
                .andExpect(jsonPath("$[0].ownerLogin", is("octocat")))
                .andExpect(jsonPath("$[0].branches", hasSize(1)))
                .andExpect(jsonPath("$[0].branches[0].name", is("master")))
                .andExpect(jsonPath("$[0].branches[0].commitSha", is("c5b97d5ae6c19d5c5df71a34c7fbeeda2479ccbc")));
    }

    @Test
    public void should_ReturnNotFound_When_UserDoesNotExist() throws Exception {
        // Given
        when(githubService.userExists("octocat"))
                .thenReturn(false);

        // When
        var resultActions = this.mockMvc.perform(get("/repos?username=octocat"))
                .andDo(print());

        // Then
        resultActions
                .andExpect(status().isNotFound());
    }

    @Test
    public void should_ReturnEmptyList_When_ReposAreEmpty() throws Exception {
        // Given
        when(githubService.userExists("octocat"))
                .thenReturn(true);
        when(githubService.getGitHubRepos("octocat"))
                .thenReturn(new GitHubRepo[0]);

        // When
        var resultActions = this.mockMvc.perform(get("/repos?username=octocat"))
                .andDo(print());

        // Then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void should_ReturnReposWithEmptyBranches_When_BranchesAreEmpty() throws Exception {
        // Given
        when(githubService.userExists("octocat"))
                .thenReturn(true);
        when(githubService.getGitHubRepos("octocat"))
                .thenReturn(gitHubRepos);
        when(githubService.getGitHubBranches("octocat", "Hello-World"))
                .thenReturn(new GitHubBranch[0]);

        // When
        var resultActions = this.mockMvc.perform(get("/repos?username=octocat"))
                .andDo(print());

        // Then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Hello-World")))
                .andExpect(jsonPath("$[0].ownerLogin", is("octocat")))
                .andExpect(jsonPath("$[0].branches", hasSize(0)));
    }
}
