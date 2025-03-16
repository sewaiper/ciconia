package ru.sewaiper.ciconia.client.tdlib.action;

import org.drinkless.tdlib.TdApi;
import org.springframework.stereotype.Component;
import ru.sewaiper.ciconia.client.tdlib.ResponseAction;
import ru.sewaiper.ciconia.service.message.MessageService;

@Component
public class UpdateLastMessage implements ResponseAction {

    private final MessageService messageService;

    public UpdateLastMessage(MessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    public int code() {
        return TdApi.UpdateChatLastMessage.CONSTRUCTOR;
    }

    @Override
    public void execute(TdApi.Object object) {
        var payload = (TdApi.UpdateChatLastMessage) object;
        var message = payload.lastMessage;

        if (message == null || message.content == null) {
            return;
        }

        messageService.onUpdate(payload);
    }
}
