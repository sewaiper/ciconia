package ru.sewaiper.ciconia.service.message;

import org.apache.commons.lang3.StringUtils;
import org.drinkless.tdlib.TdApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import ru.sewaiper.ciconia.model.Channel;
import ru.sewaiper.ciconia.model.Message;
import ru.sewaiper.ciconia.repository.MessageRepository;
import ru.sewaiper.ciconia.service.channel.ChannelService;
import ru.sewaiper.ciconia.service.message.packet.PacketService;
import ru.sewaiper.ciconia.service.message.tag.CiconiaTag;
import ru.sewaiper.ciconia.service.message.tag.TagService;
import ru.sewaiper.ciconia.service.message.brief.BriefService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@SuppressWarnings("rawtypes")
public class DefaultMessageService implements MessageService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultMessageService.class);

    private final Map<Integer, TextExtractor> extractors = new HashMap<>();

    private final ChannelService channelService;
    private final BriefService briefService;
    private final TagService tagService;
    private final PacketService packetService;

    private final MessageRepository messageRepository;

    private final PlatformTransactionManager transactionManager;

    public DefaultMessageService(List<TextExtractor> extractors,
                                 ChannelService channelService,
                                 BriefService briefService,
                                 TagService tagService,
                                 PacketService packetService,
                                 MessageRepository messageRepository,
                                 PlatformTransactionManager transactionManager) {

        this.channelService = channelService;
        this.briefService = briefService;
        this.tagService = tagService;
        this.packetService = packetService;
        this.messageRepository = messageRepository;
        this.transactionManager = transactionManager;

        extractors.forEach(extractor -> this.extractors.put(extractor.code(), extractor));
    }

    @Override
    public void onUpdate(@NonNull TdApi.UpdateChatLastMessage event) {
        try {
            //noinspection DataFlowIssue
            new TransactionTemplate(transactionManager)
                    .execute(status -> doProcess(event))
                    .ifPresent(packetService::append);
        } catch (Exception e) {
            LOG.error("Unable to process update last message event", e);
        }
    }

    private Optional<Message> doProcess(@NonNull TdApi.UpdateChatLastMessage event) {
        var channel = channelService.getChannel(event.chatId);
        if (channel.isEmpty()) {
            return Optional.empty();
        }

        if (messageRepository.existsById(event.lastMessage.id)) {
            return Optional.empty();
        }

        var text = extractText(event.lastMessage);

        if (text.isEmpty()) {
            LOG.warn("Unable process message {} from chat {}, because text is missing",
                    event.lastMessage.id, channel.get().getName());

            return Optional.empty();
        }

        var entity = saveMessage(
                toEntity(event.lastMessage, channel.get(), text.get())
        );

        return Optional.of(entity);
    }

    private Optional<String> extractText(TdApi.Message message) {
        var content = message.content;

        var extractor = extractors.get(content.getConstructor());
        if (extractor == null) {
            LOG.warn(
                    "Unable to extract text from message {}:{}. Extractor for {} is missing",
                    message.chatId,
                    message.id,
                    content.getClass().getSimpleName()
            );
            return Optional.empty();
        }

        //noinspection unchecked
        var text = extractor.extract(content);

        return Optional.ofNullable(text)
                .filter(StringUtils::isNotBlank);
    }

    private Message toEntity(TdApi.Message message, Channel channel, String text) {
        var entity = new Message();
        entity.setId(message.id);
        entity.setChannelId(channel.getId());
        entity.setLink(String.format("https://t.me/%s/%d", channel.getName(), message.id));
        entity.setBrief(briefService.getBrief(text));
        entity.setText(text);
        return entity;
    }

    private Message saveMessage(Message entity) {
        var tag = tagService.tagging(entity.getId(), entity.getText())
                        .map(CiconiaTag::ordinal).orElse(null);

        messageRepository.insert(
                entity.getId(), entity.getChannelId(),
                entity.getLink(), entity.getBrief(),
                entity.getText(), tag
        );

        return entity;
    }
}
