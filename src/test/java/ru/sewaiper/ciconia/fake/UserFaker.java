package ru.sewaiper.ciconia.fake;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import ru.sewaiper.ciconia.model.User;

public final class UserFaker {

    private UserFaker() {
    }

    public static User entity() {
        var entity = new User();
        entity.setId(RandomUtils.insecure().randomInt());
        entity.setUsername(RandomStringUtils.insecure().nextAlphabetic(5));
        entity.setFirstName(RandomStringUtils.insecure().nextAlphabetic(5));
        entity.setLastName(RandomStringUtils.insecure().nextAlphabetic(5));
        entity.setChatId(RandomUtils.insecure().randomInt());
        return entity;
    }
}
