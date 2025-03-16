package ru.sewaiper.ciconia.mapper;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.sewaiper.ciconia.model.User;

@Component
public class UserMapper {

    public User toEntity(Message message) {
        var user = message.getFrom();

        var entity = new User();
        entity.setId(user.getId());
        entity.setUsername(user.getUserName());
        entity.setFirstName(user.getFirstName());
        entity.setLastName(user.getLastName());
        entity.setChatId(message.getChatId());
        return entity;
    }
}
