package ru.sewaiper.ciconia.service.message.extractor;

import org.drinkless.tdlib.TdApi;
import org.springframework.stereotype.Component;
import ru.sewaiper.ciconia.service.message.TextExtractor;

import java.util.Optional;

@Component
public class VideoCaptionExtractor implements TextExtractor<TdApi.MessageVideo> {

    @Override
    public int code() {
        return TdApi.MessageVideo.CONSTRUCTOR;
    }

    @Override
    public String extract(TdApi.MessageVideo content) {
        return Optional.ofNullable(content.caption)
                .map(ft -> ft.text)
                .orElse(null);
    }
}
