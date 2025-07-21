package pl.com.foks.reporest;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.util.List;

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

    @Test
    public void should_ReturnValidObjects_When_DeserializingJsons() throws IOException {
        // Given
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // When
        var githubRepos = objectMapper.readValue(GithubMockTests.class.getResourceAsStream("/responses/github-repos.json"), GithubRepo[].class);
        var githubBranches = objectMapper.readValue(GithubMockTests.class.getResourceAsStream("/responses/github-branches.json"), GithubBranch[].class);

        // Then
        assertThat(githubRepos).isNotNull();
        assertThat(githubRepos).isNotEmpty();
        assertThat(githubBranches).isNotNull();
        assertThat(githubBranches).isNotEmpty();
    }

    @Test
    public void should_ReturnRepositoryWithBranches_When_UserExists() throws Exception {
        // Given
        GithubReposResponse.Branch branch = GithubReposResponse.Branch.builder()
                .name("master")
                .commitSha("c5b97d5ae6c19d5c5df71a34c7fbeeda2479ccbc")
                .build();
                
        GithubReposResponse.Repo repo = GithubReposResponse.Repo.builder()
                .name("Hello-World")
                .ownerLogin("octocat")
                .branches(List.of(branch))
                .build();
                
        GithubReposResponse response = GithubReposResponse.builder()
                .repositories(List.of(repo))
                .build();
                
        when(githubService.getUserRepositoriesNotForksWithBranches("octocat")).thenReturn(response);

        // When
        var resultActions = this.mockMvc.perform(get("/repos?username=octocat"))
                .andDo(print());

        // Then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.repositories", hasSize(1)))
                .andExpect(jsonPath("$.repositories[0].name", is("Hello-World")))
                .andExpect(jsonPath("$.repositories[0].ownerLogin", is("octocat")))
                .andExpect(jsonPath("$.repositories[0].branches", hasSize(1)))
                .andExpect(jsonPath("$.repositories[0].branches[0].name", is("master")))
                .andExpect(jsonPath("$.repositories[0].branches[0].commitSha", is("c5b97d5ae6c19d5c5df71a34c7fbeeda2479ccbc")));
    }

    @Test
    public void should_ReturnNotFound_When_UserDoesNotExist() throws Exception {
        // Given
        when(githubService.getUserRepositoriesNotForksWithBranches("octocat"))
                .thenThrow(new UserNotFoundException(HttpStatus.NOT_FOUND, "User not found: octocat"));

        // When
        var resultActions = this.mockMvc.perform(get("/repos?username=octocat"))
                .andDo(print());

        // Then
        resultActions
                .andExpect(status().isNotFound());
    }
}
