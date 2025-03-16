package ru.sewaiper.ciconia.service.message;

import org.drinkless.tdlib.TdApi;

public interface TextExtractor<T extends TdApi.MessageContent> {

    int code();

    String extract(T content);
}
