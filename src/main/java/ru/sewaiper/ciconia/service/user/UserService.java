package ru.sewaiper.ciconia.service.user;

import ru.sewaiper.ciconia.bot.command.CommandType;
import ru.sewaiper.ciconia.model.User;

import java.util.Optional;

public interface UserService {

    Optional<User> getUser(long id);

    void saveUser(User user);

    void setCommand(long userId, CommandType command);
}
