package ru.sewaiper.ciconia.service.message.extractor;

import org.drinkless.tdlib.TdApi;
import org.springframework.stereotype.Component;
import ru.sewaiper.ciconia.service.message.TextExtractor;

@Component
public class PlainTextExtractor implements TextExtractor<TdApi.MessageText> {

    @Override
    public int code() {
        return TdApi.MessageText.CONSTRUCTOR;
    }

    @Override
    public String extract(TdApi.MessageText content) {
        return content.text.text;
    }
}
