package ru.sewaiper.ciconia.service.user;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sewaiper.ciconia.bot.command.CommandType;
import ru.sewaiper.ciconia.model.User;
import ru.sewaiper.ciconia.repository.UserRepository;

import java.util.Optional;

@Service
public class DefaultUserService implements UserService {

    private final UserRepository userRepository;

    public DefaultUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<User> getUser(long id) {
        return userRepository.findById(id);
    }

    @Override
    @Transactional
    public void saveUser(User user) {
        userRepository.insert(
                user.getId(),
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                CommandType.START.name(),
                user.getChatId()
        );
    }

    @Override
    public void setCommand(long userId, CommandType command) {
        userRepository.updateCommand(userId, command.name());
    }
}
