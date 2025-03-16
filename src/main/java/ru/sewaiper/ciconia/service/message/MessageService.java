package ru.sewaiper.ciconia.service.message;

import org.drinkless.tdlib.TdApi;
import org.springframework.lang.NonNull;

public interface MessageService {

    void onUpdate(@NonNull TdApi.UpdateChatLastMessage message);
}
