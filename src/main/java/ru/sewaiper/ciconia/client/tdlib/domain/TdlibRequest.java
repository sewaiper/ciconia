package ru.sewaiper.ciconia.client.tdlib.domain;

import org.drinkless.tdlib.Client;
import org.drinkless.tdlib.TdApi;

public record TdlibRequest(TdApi.Function<?> function,
                           Client.ResultHandler handler) {
}
