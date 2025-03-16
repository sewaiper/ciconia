package ru.sewaiper.ciconia.client.yandex;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import ru.sewaiper.ciconia.client.yandex.domain.ClassificationRequest;
import ru.sewaiper.ciconia.client.yandex.domain.ClassificationResponse;
import ru.sewaiper.ciconia.configuration.properties.YandexProperties;

import java.util.List;

@Component
public class YandexDomainClient extends YandexAuthClient implements YandexClient {

    private final YandexProperties properties;

    public YandexDomainClient(YandexProperties properties) {
        super(properties);
        this.properties = properties;
    }

    @Override
    public ClassificationResponse classification(List<String> labels, String text) {

        var request = new ClassificationRequest(
                String.format("cls://%s/yandexgpt/latest", properties.getCatalogId()),
                "Определи к каким секторам принадлежит новость",
                labels, text
        );

        var entity = getRestClient().method(HttpMethod.POST)
                .uri("https://llm.api.cloud.yandex.net/foundationModels/v1/fewShotTextClassification")
                .header(HttpHeaders.AUTHORIZATION, getIamToken())
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toEntity(ClassificationResponse.class);

        return entity.getBody();
    }
}
