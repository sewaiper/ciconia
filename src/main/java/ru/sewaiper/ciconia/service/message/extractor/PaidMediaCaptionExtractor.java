package ru.sewaiper.ciconia.service.message.extractor;

import org.drinkless.tdlib.TdApi;
import org.springframework.stereotype.Component;
import ru.sewaiper.ciconia.service.message.TextExtractor;

import java.util.Optional;

@Component
public class PaidMediaCaptionExtractor implements TextExtractor<TdApi.MessagePaidMedia> {

    @Override
    public int code() {
        return TdApi.MessagePaidMedia.CONSTRUCTOR;
    }

    @Override
    public String extract(TdApi.MessagePaidMedia content) {
        return Optional.ofNullable(content.caption)
                .map(ft -> ft.text)
                .orElse(null);
    }
}
