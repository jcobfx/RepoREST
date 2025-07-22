package pl.com.foks.reporest;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GithubController.class)
public class GithubMockTests {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GithubService githubService;

    private static GithubRepo[] githubRepos;
    private static GithubBranch[] githubBranches;

    @BeforeAll
    public static void setUp() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        githubRepos = objectMapper.readValue(GithubMockTests.class.getResourceAsStream("/responses/github-repos.json"), GithubRepo[].class);
        githubBranches = objectMapper.readValue(GithubMockTests.class.getResourceAsStream("/responses/github-branches.json"), GithubBranch[].class);

        assertThat(githubRepos).isNotNull();
        assertThat(githubBranches).isNotNull();
    }

    @Test
    public void should_ReturnRepositoryWithBranches_When_UserExists() throws Exception {
        // Given
        when(githubService.userExists("octocat")).thenReturn(true);
        when(githubService.getGithubRepos("octocat")).thenReturn(ResponseEntity.ok(githubRepos));
        when(githubService.getGithubBranches("octocat", "Hello-World"))
                .thenReturn(ResponseEntity.ok(githubBranches));

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
    public void should_ThrowReposNotFoundException_When_ReposAreNull() throws Exception {
        // Given
        when(githubService.userExists("octocat"))
                .thenReturn(true);
        when(githubService.getGithubRepos("octocat"))
                .thenReturn(ResponseEntity.ok(null));

        // When
        var resultActions = this.mockMvc.perform(get("/repos?username=octocat"))
                .andDo(print());

        // Then
        resultActions
                .andExpect(status().isNotFound());
    }

    @Test
    public void should_ThrowBranchesNotFoundException_When_BranchesAreNull() throws Exception {
        // Given
        when(githubService.userExists("octocat"))
                .thenReturn(true);
        when(githubService.getGithubRepos("octocat"))
                .thenReturn(ResponseEntity.ok(githubRepos));
        when(githubService.getGithubBranches("octocat", "Hello-World"))
                .thenReturn(ResponseEntity.ok(null));

        // When
        var resultActions = this.mockMvc.perform(get("/repos?username=octocat"))
                .andDo(print());

        // Then
        resultActions
                .andExpect(status().isNotFound());
    }
}
