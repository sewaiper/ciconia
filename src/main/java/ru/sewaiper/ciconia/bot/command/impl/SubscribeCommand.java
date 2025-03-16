package ru.sewaiper.ciconia.bot.command.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.sewaiper.ciconia.bot.command.CiconiaCommand;
import ru.sewaiper.ciconia.bot.command.CommandType;
import ru.sewaiper.ciconia.bot.CiconiaMessageTo;
import ru.sewaiper.ciconia.client.tdlib.error.SubscribeException;
import ru.sewaiper.ciconia.service.channel.ChannelService;
import ru.sewaiper.ciconia.service.user.UserService;

import java.util.Optional;

@Component
public class SubscribeCommand implements CiconiaCommand {

    private static final Logger LOG = LoggerFactory.getLogger(SubscribeCommand.class);

    private final ChannelService channelService;
    private final UserService userService;

    public SubscribeCommand(ChannelService channelService, UserService userService) {
        this.channelService = channelService;
        this.userService = userService;
    }

    @Override
    public CommandType command() {
        return CommandType.SUBSCRIBE;
    }

    @Override
    public String getDescription() {
        return "Добавить канал для обработки";
    }

    @Override
    public Optional<CiconiaMessageTo> apply(Message message) {
        var userId = message.getFrom().getId();

        userService.setCommand(userId, CommandType.SUBSCRIBE);

        return Optional.of(
                new CiconiaMessageTo(
                        message.getChatId(),  applyText()
                )
        );
    }

    private String applyText() {
        return "Хорошо. Отправь мне ссылки на каналы, которые ты хочешь включить в рассылку";
    }

    @Override
    public Optional<CiconiaMessageTo> execute(Message message) {
        var link = message.getText();
        var userId = message.getFrom().getId();
        try {
            channelService.subscribe(userId, link);
            LOG.info("Successful subscribed on channel {}", link);
            return Optional.empty();
        } catch (SubscribeException e) {
            LOG.error("Unable to subscribe on channel {}", link, e);
            return Optional.of(
                    new CiconiaMessageTo(
                            message.getChatId(),
                            message.getMessageId(),
                            "Упс. Что-то пошло не так"
                    )
            );
        }
    }
}
