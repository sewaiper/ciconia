package ru.sewaiper.ciconia.bot;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.sewaiper.ciconia.bot.command.CommandType;
import ru.sewaiper.ciconia.bot.command.CiconiaCommand;
import ru.sewaiper.ciconia.configuration.properties.TgBotProperties;
import ru.sewaiper.ciconia.bot.error.CiconiaException;
import ru.sewaiper.ciconia.bot.error.SendMessageException;
import ru.sewaiper.ciconia.service.user.UserService;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.removeStart;
import static org.apache.commons.lang3.StringUtils.toRootLowerCase;
import static org.apache.commons.lang3.StringUtils.toRootUpperCase;

@Component
public class CiconiaBot extends TelegramLongPollingBot {

    private final Map<CommandType, CiconiaCommand> commands = new EnumMap<>(CommandType.class);

    private final TgBotProperties properties;
    private final UserService userService;

    public CiconiaBot(TgBotProperties properties,
                      CiconiaBotOptions options,
                      List<CiconiaCommand> commands,
                      UserService userService) {

        super(options, properties.getToken());

        this.properties = properties;
        this.userService = userService;
        commands.forEach(cmd -> this.commands.put(cmd.command(), cmd));
    }

    public void setCommands() throws TelegramApiException {
        var newest = commands.keySet()
                .stream()
                .map(commands::get)
                .map(this::toRequest).toList();

        var request = new SetMyCommands();
        request.setCommands(newest);
        execute(request);
    }

    private BotCommand toRequest(CiconiaCommand command) {
        var request = new BotCommand();
        request.setCommand(StringUtils.toRootLowerCase(command.command().name()));
        request.setDescription(command.getDescription());
        return request;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage()) {
            return;
        }

        var message = update.getMessage();

        try {
            validateUser(message);

            var command = getCommand(message);

            if (command.isPresent()) {
                command.get().apply(message)
                        .ifPresent(this::sendMessage);
            } else {
                tryExecuteCurrentCommand(message);
            }

        } catch (CiconiaException e) {
            sendMessage(new CiconiaMessageTo(
                    message.getChatId(),
                    message.getMessageId(), e.getMessage()
            ));
        }
    }

    @Override
    public String getBotUsername() {
        return properties.getName();
    }

    public void sendMessage(CiconiaMessageTo message) {
        var request = new SendMessage();
        request.setChatId(message.chatId());
        request.setText(message.text());
        request.setReplyToMessageId(message.replyMessageId());

        try {
            execute(request);
        } catch (TelegramApiException e) {
            throw new SendMessageException("", e);
        }
    }

    private void validateUser(Message message) {
        var user = message.getFrom();

        if (user == null) {
            throw new CiconiaException(
                    "Не могу идентифцировать пользователя. " +
                            "В сообщении отсутствует ID пользователя"
            );
        }
    }

    private Optional<CiconiaCommand> getCommand(Message message) {
        var candidate = prepareCommand(message.getText());

        if (StringUtils.isBlank(candidate)) {
            return Optional.empty();
        }

        try {
            var type = CommandType.valueOf(candidate);
            return Optional.of(this.commands.get(type));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    private String prepareCommand(String command) {
        command = removeStart(command, '/');
        command = toRootUpperCase(command);
        return command;
    }

    private void tryExecuteCurrentCommand(Message message) {

        var user = userService.getUser(message.getFrom().getId());

        if (user.isEmpty()) {
            throw new CiconiaException(
                    "Не могу определить пользователя. Введите команду /" +
                            toRootLowerCase(CommandType.START.name()) +
                            " для регистрации"
            );
        }

        var commandType = user.get().getCommand();
        if (commandType == null) {
            return;
        }

        var command = commands.get(commandType);
        if (command == null) {
            return;
        }

        command.execute(message).ifPresent(this::sendMessage);
    }
}
