package ru.sewaiper.ciconia.service.message.extractor;

import org.drinkless.tdlib.TdApi;
import org.springframework.stereotype.Component;
import ru.sewaiper.ciconia.service.message.TextExtractor;

import java.util.Optional;

@Component
public class DocumentCaptionExtractor implements TextExtractor<TdApi.MessageDocument> {

    @Override
    public int code() {
        return TdApi.MessageDocument.CONSTRUCTOR;
    }

    @Override
    public String extract(TdApi.MessageDocument content) {
        return Optional.ofNullable(content.caption)
                .map(ft -> ft.text)
                .orElse(null);
    }
}
