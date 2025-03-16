package ru.sewaiper.ciconia.service.message.extractor;

import org.drinkless.tdlib.TdApi;
import org.springframework.stereotype.Component;
import ru.sewaiper.ciconia.service.message.TextExtractor;

import java.util.Optional;

@Component
public class PhotoCaptionExtractor implements TextExtractor<TdApi.MessagePhoto> {

    @Override
    public int code() {
        return TdApi.MessagePhoto.CONSTRUCTOR;
    }

    @Override
    public String extract(TdApi.MessagePhoto content) {
        return Optional.ofNullable(content.caption)
                .map(ft -> ft.text)
                .orElse(null);
    }
}
