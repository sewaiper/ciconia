package ru.sewaiper.ciconia.service.message.extractor;

import org.drinkless.tdlib.TdApi;
import org.springframework.stereotype.Component;
import ru.sewaiper.ciconia.service.message.TextExtractor;

import java.util.Optional;

@Component
public class AudioCaptionExtractor implements TextExtractor<TdApi.MessageAudio> {

    @Override
    public int code() {
        return TdApi.MessageAudio.CONSTRUCTOR;
    }

    @Override
    public String extract(TdApi.MessageAudio content) {
        return Optional.ofNullable(content.caption)
                .map(ft -> ft.text)
                .orElse(null);
    }
}
