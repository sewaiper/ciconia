package ru.sewaiper.ciconia.service.message.extractor;

import org.drinkless.tdlib.TdApi;
import org.springframework.stereotype.Component;
import ru.sewaiper.ciconia.service.message.TextExtractor;

import java.util.Optional;

@Component
public class VoiceNoteCaptionExtractor implements TextExtractor<TdApi.MessageVoiceNote> {

    @Override
    public int code() {
        return TdApi.MessageVoiceNote.CONSTRUCTOR;
    }

    @Override
    public String extract(TdApi.MessageVoiceNote content) {
        return Optional.ofNullable(content.caption)
                .map(ft -> ft.text)
                .orElse(null);
    }
}
