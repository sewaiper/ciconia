package ru.sewaiper.ciconia.service.auth;

import org.springframework.lang.NonNull;

public interface TdlibAuthService {

    void setAppParams();

    void setPhoneNumber(@NonNull String phoneNumber);

    void setTgCode(String code);

    TdlibAuth getState();

    void setState(TdlibAuth state);
}
