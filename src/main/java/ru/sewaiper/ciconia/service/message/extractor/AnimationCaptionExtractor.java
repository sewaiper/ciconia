package ru.sewaiper.ciconia.service.message.extractor;

import org.drinkless.tdlib.TdApi;
import org.springframework.stereotype.Component;
import ru.sewaiper.ciconia.service.message.TextExtractor;

import java.util.Optional;

@Component
public class AnimationCaptionExtractor implements TextExtractor<TdApi.MessageAnimation> {

    @Override
    public int code() {
        return TdApi.MessageAnimation.CONSTRUCTOR;
    }

    @Override
    public String extract(TdApi.MessageAnimation content) {
        return Optional.ofNullable(content.caption)
                .map(ft -> ft.text)
                .orElse(null);
    }
}

