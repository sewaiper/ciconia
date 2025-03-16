package ru.sewaiper.ciconia.client.yandex;

import ru.sewaiper.ciconia.client.yandex.domain.ClassificationResponse;

import java.util.List;

public interface YandexClient {

    ClassificationResponse classification(List<String> labels, String text);
}
