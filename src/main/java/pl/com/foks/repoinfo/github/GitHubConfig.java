package pl.com.foks.repoinfo.github;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Configuration
public class GitHubConfig {

    @Value("${github.api.url}")
    private String githubApiUrl;

    @Value("${github.api.version}")
    private String githubApiVersion;

    @Value("${github.api.data-type}")
    private String githubApiDataType;

    @Bean(name = "gitHubRestClient")
    public RestClient gitHubRestClient() {
        return RestClient.builder()
                .baseUrl(githubApiUrl)
                .defaultHeaders(headers -> headers
                        .putAll(Map.of(
                                "Accept", List.of(githubApiDataType),
                                "X-GitHub-Api-Version", List.of(githubApiVersion)
                        )))
                .build();
    }

}
