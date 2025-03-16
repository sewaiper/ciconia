package ru.sewaiper.ciconia.service.channel;

import org.drinkless.tdlib.TdApi;
import ru.sewaiper.ciconia.model.Channel;

import java.util.Optional;

public interface ChannelService {

    TdApi.Chat searchChat(String link);

    Optional<Channel> getChannel(long chatId);

    void subscribe(long userId, String link);
}
