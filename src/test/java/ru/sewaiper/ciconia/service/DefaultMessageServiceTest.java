package ru.sewaiper.ciconia.service;

import org.apache.commons.collections4.IterableUtils;
import org.drinkless.tdlib.TdApi;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import ru.sewaiper.ciconia.BaseTest;
import ru.sewaiper.ciconia.bot.CiconiaMessageTo;
import ru.sewaiper.ciconia.db.QueryLogger;
import ru.sewaiper.ciconia.fake.MessageFaker;
import ru.sewaiper.ciconia.fake.TagFaker;
import ru.sewaiper.ciconia.fake.TelegraphFaker;
import ru.sewaiper.ciconia.repository.MessageRepository;
import ru.sewaiper.ciconia.repository.packet.PacketRepository;
import ru.sewaiper.ciconia.service.message.DefaultMessageService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class DefaultMessageServiceTest extends BaseTest {

    @Autowired
    private DefaultMessageService messageService;

    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private PacketRepository packetRepository;

    @Test
    void onUpdate_successTest() {
        var user = createUser();
        createPacketConfig(user.getId(), 1);
        var channel = createChannel();
        subscribe(user.getId(), channel.getId());

        var update = MessageFaker.lastMessage();

        update.chatId = channel.getId();
        update.lastMessage.chatId = channel.getId();

        var predictions = TagFaker.allPredict();
        doReturn(predictions).when(yandexClient)
                .classification(anyList(), anyString());

        var telegraphPage = TelegraphFaker.page();
        doReturn(telegraphPage)
                .when(telegraphClient).createPage(anyString(), anyList());

        QueryLogger.enable();

        messageService.onUpdate(update);

        QueryLogger.disable();

        var message = messageRepository.findById(update.lastMessage.id);
        assertNotNull(message);
        assertTrue(message.isPresent());

        assertEquals(channel.getId(), message.get().getChannelId());
        assertEquals(
                String.format("https://t.me/%s/%d", channel.getName(), update.lastMessage.id),
                message.get().getLink()
        );

        assertNotNull(message.get().getBrief());
        assertTrue(message.get().getBrief().endsWith("..."));
        assertNotNull(message.get().getText());

        var packets = IterableUtils.toList(packetRepository.findAll());
        assertNotNull(packets);
        assertEquals(2, packets.size());
        assertEquals(user.getId(), packets.get(0).getUserId());
        assertEquals(user.getId(), packets.get(1).getUserId());

        var content = packetRepository.findContent(packets.get(0).getId());
        assertNotNull(content);
        assertEquals(1, content.size());

        var chapter = ArgumentCaptor.forClass(CiconiaMessageTo.class);
        verify(ciconiaBot, times(1))
                .sendMessage(chapter.capture());

        assertNotNull(chapter.getValue());
        assertEquals(user.getChatId(), chapter.getValue().chatId());
        assertEquals(telegraphPage.getResult().url(), chapter.getValue().text());
    }

    @Test
    void unsubscribed_successTest() {
        var user = createUser();
        createPacketConfig(user.getId(), 1);

        var update = MessageFaker.lastMessage();

        QueryLogger.enable();

        messageService.onUpdate(update);

        QueryLogger.disable();

        var message = messageRepository.findById(update.lastMessage.id);
        assertNotNull(message);
        assertFalse(message.isPresent());

        var packets = IterableUtils.toList(packetRepository.findAll());
        assertNotNull(packets);
        assertTrue(packets.isEmpty());
    }

    @Test
    void alreadyProcessed_successTest() {
        var user = createUser();
        createPacketConfig(user.getId(), 1);
        var channel = createChannel();
        subscribe(user.getId(), channel.getId());

        var message = createMessage(channel.getId());

        var update = MessageFaker.lastMessage();

        update.chatId = channel.getId();
        update.lastMessage.id = message.getId();
        update.lastMessage.chatId = channel.getId();

        QueryLogger.enable();

        messageService.onUpdate(update);

        QueryLogger.disable();

        var packets = IterableUtils.toList(packetRepository.findAll());
        assertNotNull(packets);
        assertTrue(packets.isEmpty());
    }

    @Test
    void emptyText_successTest() {
        var user = createUser();
        createPacketConfig(user.getId(), 1);
        var channel = createChannel();
        subscribe(user.getId(), channel.getId());

        var update = MessageFaker.lastMessage();

        var text = MessageFaker.textContent();
        text.text = new TdApi.FormattedText();

        update.chatId = channel.getId();
        update.lastMessage.chatId = channel.getId();
        update.lastMessage.content = text;

        QueryLogger.enable();

        messageService.onUpdate(update);

        QueryLogger.disable();

        var message = messageRepository.findById(update.lastMessage.id);
        assertNotNull(message);
        assertFalse(message.isPresent());

        var packets = IterableUtils.toList(packetRepository.findAll());
        assertNotNull(packets);
        assertTrue(packets.isEmpty());
    }

    @Test
    void extractorNotFound_successTest() {
        var user = createUser();
        createPacketConfig(user.getId(), 1);
        var channel = createChannel();
        subscribe(user.getId(), channel.getId());

        var update = MessageFaker.lastMessage();

        var text = new SomethingContent();

        update.chatId = channel.getId();
        update.lastMessage.chatId = channel.getId();
        update.lastMessage.content = text;

        QueryLogger.enable();

        messageService.onUpdate(update);

        QueryLogger.disable();

        var message = messageRepository.findById(update.lastMessage.id);
        assertNotNull(message);
        assertFalse(message.isPresent());

        var packets = IterableUtils.toList(packetRepository.findAll());
        assertNotNull(packets);
        assertTrue(packets.isEmpty());
    }

    @Test
    void taggingError_failureTest() {
        var user = createUser();
        createPacketConfig(user.getId(), 1);
        var channel = createChannel();
        subscribe(user.getId(), channel.getId());

        var update = MessageFaker.lastMessage();

        update.chatId = channel.getId();
        update.lastMessage.chatId = channel.getId();

        doThrow(new IllegalStateException("Unable set tag for message"))
                .when(yandexClient).classification(anyList(), anyString());

        QueryLogger.enable();

        messageService.onUpdate(update);

        QueryLogger.disable();

        var message = messageRepository.findById(update.lastMessage.id);
        assertNotNull(message);
        assertFalse(message.isPresent());

        var packets = IterableUtils.toList(packetRepository.findAll());
        assertNotNull(packets);
        assertTrue(packets.isEmpty());
    }

    private static class SomethingContent extends TdApi.MessageContent {
        @Override
        public int getConstructor() {
            return Integer.MAX_VALUE;
        }
    }
}
