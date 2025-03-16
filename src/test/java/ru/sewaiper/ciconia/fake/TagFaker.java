package ru.sewaiper.ciconia.fake;

import org.apache.commons.lang3.RandomUtils;
import ru.sewaiper.ciconia.client.yandex.domain.ClassificationResponse;
import ru.sewaiper.ciconia.client.yandex.domain.ClassificationResponse.Prediction;
import ru.sewaiper.ciconia.service.message.tag.CiconiaTag;

import java.util.Arrays;

public final class TagFaker {

    private TagFaker() {
    }

    public static ClassificationResponse allPredict() {
        return predict(
                Arrays.stream(CiconiaTag.values())
                        .map(TagFaker::prediction)
                        .toArray(Prediction[]::new)
        );
    }

    public static ClassificationResponse predict(Prediction... predictions) {
        return new ClassificationResponse(
                "yagpt",
                Arrays.stream(predictions).toList()
        );
    }

    public static Prediction prediction(CiconiaTag tag) {
        return prediction(tag, RandomUtils.insecure().randomDouble(0, 1.));
    }

    public static Prediction prediction(CiconiaTag tag, double confidence) {
        return new Prediction(tag.label(), confidence);
    }

    public static CiconiaTag tag() {
        var values = CiconiaTag.values();
        return values[RandomUtils.insecure().randomInt(0, values.length)];
    }
}
