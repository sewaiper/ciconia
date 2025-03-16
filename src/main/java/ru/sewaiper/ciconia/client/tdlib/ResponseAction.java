package ru.sewaiper.ciconia.client.tdlib;

import org.drinkless.tdlib.TdApi;

public interface ResponseAction {

    int code();

    void execute(TdApi.Object object);
}
