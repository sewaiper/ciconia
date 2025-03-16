package ru.sewaiper.ciconia.service.message.tag;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sewaiper.ciconia.client.yandex.YandexClient;

import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;

@Service
public class YagptTagService implements TagService {

    private final YandexClient client;

    public YagptTagService(YandexClient client) {
        this.client = client;
    }

    @Override
    @Transactional
    public Optional<CiconiaTag> tagging(long messageId, String text) {
        var response = client.classification(CiconiaTag.labels(), text);

        return response.predictions()
                .stream()
                .filter(Objects::nonNull)
                .filter(it -> it.confidence() != null)
                .max(Comparator.naturalOrder())
                .map(it -> CiconiaTag.fromLabel(it.label()));
    }
}
