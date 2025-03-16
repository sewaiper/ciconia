package ru.sewaiper.ciconia.client.yandex.domain;

import java.util.List;

public record ClassificationResponse(String modelVersion,
                                     List<Prediction> predictions) {

    public record Prediction(String label,
                             Double confidence) implements Comparable<Prediction> {

        @Override
        public int compareTo(Prediction o) {
            return confidence.compareTo(o.confidence);
        }
    }
}
