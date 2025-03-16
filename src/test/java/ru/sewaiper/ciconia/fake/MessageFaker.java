package ru.sewaiper.ciconia.fake;

import com.thedeanda.lorem.Lorem;
import com.thedeanda.lorem.LoremIpsum;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.drinkless.tdlib.TdApi;
import ru.sewaiper.ciconia.model.Message;

public final class MessageFaker {

    private static final Lorem LOREM = LoremIpsum.getInstance();

    private MessageFaker() {
    }

    public static TdApi.UpdateChatLastMessage lastMessage() {
        var update = new TdApi.UpdateChatLastMessage();
        update.chatId = RandomUtils.insecure().randomInt();

        var message = message();
        message.chatId = update.chatId;

        update.lastMessage = message;

        return update;
    }

    public static TdApi.Message message() {
        var message = new TdApi.Message();
        message.id = RandomUtils.insecure().randomInt();
        message.content = textContent();
        return message;
    }

    public static TdApi.MessageText textContent() {
        var content = new TdApi.MessageText();

        var text = new TdApi.FormattedText();
        text.text = LOREM.getParagraphs(1, 5);

        content.text = text;

        return content;
    }

    public static Message entity(long channelId) {
        var entity = new Message();
        entity.setId(RandomUtils.insecure().randomInt());
        entity.setChannelId(channelId);
        entity.setBrief(LOREM.getWords(5));
        entity.setText(LOREM.getWords(15));
        entity.setTag(TagFaker.tag().ordinal());

        var link = String.format(
                "https://tg.me/%s/%d",
                RandomStringUtils.insecure().nextAlphabetic(5), channelId
        );
        entity.setLink(link);
        return entity;
    }
}
