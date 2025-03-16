package ru.sewaiper.ciconia.service.channel;

import org.apache.commons.lang3.StringUtils;
import org.drinkless.tdlib.TdApi;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sewaiper.ciconia.client.tdlib.TdlibClient;
import ru.sewaiper.ciconia.client.tdlib.error.SubscribeException;
import ru.sewaiper.ciconia.model.Channel;
import ru.sewaiper.ciconia.repository.ChannelRepository;

import java.util.Optional;

@Service
public class DefaultChannelService implements ChannelService {

    private final TdlibClient client;
    private final ChannelRepository channelRepository;

    public DefaultChannelService(TdlibClient client,
                                 ChannelRepository channelRepository) {
        this.client = client;
        this.channelRepository = channelRepository;
    }

    @Override
    public TdApi.Chat searchChat(String link) {
        var request = new TdApi.SearchPublicChat();
        request.username = link;
        return client.send(request);
    }

    @Override
    public Optional<Channel> getChannel(long chatId) {
        return channelRepository.findById(chatId);
    }

    @Override
    @Transactional
    public void subscribe(long userId, String link) {
        long channelId = joinIfRequired(link);
        channelRepository.subscribe(userId, channelId);
    }

    private long joinIfRequired(String link) {
        try {
            var existed = channelRepository.findChannel(link);

            if (existed.isPresent()) {
                return existed.get().getId();
            }

            var chat = searchChat(link);

            isSuitableForSubscribe(chat);
            return joinToChat(link, chat);
        } catch (SubscribeException e) {
            throw e;
        } catch (Exception e) {
            throw new SubscribeException(e);
        }
    }

    private void isSuitableForSubscribe(TdApi.Chat chat) {
        if (TdApi.ChatTypeSupergroup.CONSTRUCTOR != chat.type.getConstructor()) {
            throw new SubscribeException("Unable to subscribe. Chat is not supergroup");
        }

        var supergroup = (TdApi.ChatTypeSupergroup) chat.type;
        if (!supergroup.isChannel) {
            throw new SubscribeException("Unable to subscribe. Chat is not channel");
        }
    }

    private long joinToChat(String name, TdApi.Chat chat) {
        var request = new TdApi.JoinChat();
        request.chatId = chat.id;
        client.send(request);

        var supergroup = (TdApi.ChatTypeSupergroup) chat.type;

        channelRepository.insert(
                chat.id,
                supergroup.supergroupId,
                StringUtils.removeStart(name, '@'),
                chat.title
        );

        return chat.id;
    }
}
