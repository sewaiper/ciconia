package ru.sewaiper.ciconia.client.telegraph;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import ru.sewaiper.ciconia.client.telegraph.domain.CreatePageRequest;
import ru.sewaiper.ciconia.client.telegraph.domain.EditAccountRequest;
import ru.sewaiper.ciconia.client.telegraph.domain.html.HtmlNode;
import ru.sewaiper.ciconia.client.telegraph.domain.response.Page;
import ru.sewaiper.ciconia.client.telegraph.domain.response.Response;
import ru.sewaiper.ciconia.configuration.properties.TelegraphProperties;

import java.util.List;

@Component
@SuppressWarnings("unused")
public class TelegraphClient {

    private final RestClient client = RestClient.create();

    private final TelegraphProperties properties;

    public TelegraphClient(TelegraphProperties properties) {
        this.properties = properties;
    }

    @NonNull
    public Response<Page> createPage(String title, List<HtmlNode> content) {
        var uri = getHost().path("createPage").build().toUri();

        var request = new CreatePageRequest(properties.getAccessToken(), title, content);

        var responseType =
                new ParameterizedTypeReference<Response<Page>>() {};

        var entity = client.method(HttpMethod.POST)
                .uri(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toEntity(responseType);

        if (entity.getBody() == null) {
            throw new IllegalStateException();
        }

        return entity.getBody();
    }

    public void editAccount(String shortName, String name, String url) {
        var uri = getHost()
                .path("editAccountInfo")
                .build().toUri();

        var request = new EditAccountRequest(
                properties.getAccessToken(), shortName, name, url
        );

        client.method(HttpMethod.POST)
                .uri(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toEntity(String.class);
    }

    private UriComponentsBuilder getHost() {
        return UriComponentsBuilder.newInstance()
                .scheme("https")
                .host("api.telegra.ph");
    }
}
