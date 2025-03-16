package ru.sewaiper.ciconia.fake;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.drinkless.tdlib.TdApi;
import ru.sewaiper.ciconia.model.Channel;

public final class ChannelFaker {

    private ChannelFaker() {}

    @SuppressWarnings("unused")
    public static TdApi.Chat chat() {
        var chat = new TdApi.Chat();
        chat.id = RandomUtils.insecure().randomLong();
        chat.type = typeChannel();
        chat.title = RandomStringUtils.insecure().nextAlphabetic(10);
        return chat;
    }

    public static TdApi.ChatType typeChannel() {
        var type = new TdApi.ChatTypeSupergroup();
        type.isChannel = true;
        type.supergroupId = RandomUtils.insecure().randomInt();
        return type;
    }

    public static Channel entity() {
        var entity = new Channel();
        entity.setId(RandomUtils.insecure().randomInt());
        entity.setSupergroupId(RandomUtils.insecure().randomInt());
        entity.setName(RandomStringUtils.insecure().nextAlphabetic(5));
        entity.setTitle(RandomStringUtils.insecure().nextAlphabetic(10));
        return entity;
    }
}
