package ru.sewaiper.ciconia.service.auth;

import org.drinkless.tdlib.TdApi;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import ru.sewaiper.ciconia.client.tdlib.TdlibClient;
import ru.sewaiper.ciconia.client.tdlib.error.AuthProcessException;
import ru.sewaiper.ciconia.configuration.properties.TdlibProperties;

import java.util.concurrent.atomic.AtomicReference;

@Service
public class DefaultTdlibAuthService implements TdlibAuthService {

    private final AtomicReference<TdlibAuth> state = new AtomicReference<>(TdlibAuth.INIT);

    private final TdlibClient client;
    private final TdlibProperties properties;

    public DefaultTdlibAuthService(TdlibClient client, TdlibProperties properties) {

        this.client = client;
        this.properties = properties;
    }

    @Override
    public void setAppParams() {
        if (state.get() != TdlibAuth.INIT) {
            throw new AuthProcessException("");
        }

        setState(TdlibAuth.WAIT_TDLIB_PARAMS);

        var request = new TdApi.SetTdlibParameters();
        request.databaseDirectory = "tdlib";
        request.useChatInfoDatabase = false;
        request.useMessageDatabase = false;
        request.apiId = properties.getId();
        request.apiHash = properties.getHash();
        request.systemLanguageCode = properties.getLanguage();
        request.deviceModel = properties.getDevice();
        request.applicationVersion = properties.getAppVersion();

        client.sendAsync(request);
    }

    @Override
    public void setPhoneNumber(@NonNull String phoneNumber) {
        if (TdlibAuth.WAIT_PHONE != state.get()) {
            throw new AuthProcessException("");
        }

        var request = new TdApi.SetAuthenticationPhoneNumber();
        request.phoneNumber = phoneNumber;

        client.sendAsync(request);
    }

    @Override
    public void setTgCode(String code) {
        if (TdlibAuth.WAIT_CODE != state.get()) {
            throw new AuthProcessException("");
        }

        var request = new TdApi.CheckAuthenticationCode();
        request.code = code;

        client.sendAsync(request);
    }

    @Override
    public TdlibAuth getState() {
        return state.get();
    }

    @Override
    public void setState(TdlibAuth state) {
        this.state.set(state);
    }
}
