package ru.sewaiper.ciconia.bot.command.impl;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.sewaiper.ciconia.bot.command.CiconiaCommand;
import ru.sewaiper.ciconia.bot.command.CommandType;
import ru.sewaiper.ciconia.bot.CiconiaMessageTo;
import ru.sewaiper.ciconia.mapper.UserMapper;
import ru.sewaiper.ciconia.service.user.UserService;

import java.util.Optional;

@Component
public class StartCommand implements CiconiaCommand {

    private final UserMapper userMapper;
    private final UserService userService;

    public StartCommand(UserMapper userMapper, UserService userService) {
        this.userMapper = userMapper;
        this.userService = userService;
    }

    @Override
    public CommandType command() {
        return CommandType.START;
    }

    @Override
    public String getDescription() {
        return "Регистрация пользователя";
    }

    @Override
    public Optional<CiconiaMessageTo> apply(Message message) {

        var user = userMapper.toEntity(message);

        userService.saveUser(user);

        return Optional.of(
                new CiconiaMessageTo(
                        message.getChatId(), getHelloText()
                )
        );
    }

    private String getHelloText() {
        return "Привет. Это инструмент для автоматизации анализа новостных каналов в Telegram";
    }

    @Override
    public Optional<CiconiaMessageTo> execute(Message message) {
        return Optional.empty();
    }
}
