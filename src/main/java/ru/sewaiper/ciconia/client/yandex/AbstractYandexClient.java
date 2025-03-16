package ru.sewaiper.ciconia.client.yandex;

import org.springframework.web.client.RestClient;

public class AbstractYandexClient {

    private final RestClient restClient;

    public AbstractYandexClient() {
        this.restClient = RestClient.create();
    }

    public RestClient getRestClient() {
        return restClient;
    }
}
