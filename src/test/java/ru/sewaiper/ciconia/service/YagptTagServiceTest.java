package ru.sewaiper.ciconia.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.sewaiper.ciconia.BaseTest;
import ru.sewaiper.ciconia.client.yandex.domain.ClassificationResponse;
import ru.sewaiper.ciconia.client.yandex.domain.ClassificationResponse.Prediction;
import ru.sewaiper.ciconia.db.QueryLogger;
import ru.sewaiper.ciconia.service.message.tag.CiconiaTag;
import ru.sewaiper.ciconia.service.message.tag.YagptTagService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;

public class YagptTagServiceTest extends BaseTest {

    @Autowired
    private YagptTagService tagService;

    @Test
    void probe() {
        var channel = createChannel();
        var message = createMessage(channel.getId());

        QueryLogger.enable();

        var response = new ClassificationResponse(
                "yagpt",
                List.of(
                        new Prediction(CiconiaTag.CONSUMER.label(), 0.),
                        new Prediction(CiconiaTag.TRANSPORT.label(), 0.9),
                        new Prediction(CiconiaTag.MEDICINE.label(), 0.0),
                        new Prediction(CiconiaTag.IT.label(), 0.5),
                        new Prediction(CiconiaTag.REAL_ESTATE.label(), 0.),
                        new Prediction(CiconiaTag.RAW_MATERIAL.label(), 0.),
                        new Prediction(CiconiaTag.TELECOMMUNICATIONS.label(), 0.2),
                        new Prediction(CiconiaTag.FINANCE.label(), 0.),
                        new Prediction(CiconiaTag.ELECTRIC_UTILITIES.label(), 0.),
                        new Prediction(CiconiaTag.ENERGY.label(), 0.8),
                        new Prediction(CiconiaTag.POLITICS.label(), 0.2)
                )
        );

        doReturn(response).when(yandexClient).classification(eq(CiconiaTag.labels()), eq(message.getText()));

        var actual = tagService.tagging(message.getId(), message.getText());

        assertNotNull(actual);
        assertTrue(actual.isPresent());
        assertEquals(CiconiaTag.TRANSPORT, actual.get());
    }
}
