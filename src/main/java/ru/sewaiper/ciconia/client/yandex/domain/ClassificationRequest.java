package ru.sewaiper.ciconia.client.yandex.domain;

import java.util.List;

public record ClassificationRequest(String modelUri,
                                    String taskDescription,
                                    List<String> labels,
                                    String text) {
}
