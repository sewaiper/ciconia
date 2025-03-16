package ru.sewaiper.ciconia.client.tdlib.action;

import org.drinkless.tdlib.TdApi;
import org.springframework.stereotype.Component;
import ru.sewaiper.ciconia.client.tdlib.ResponseAction;
import ru.sewaiper.ciconia.service.auth.TdlibAuthService;
import ru.sewaiper.ciconia.service.auth.TdlibAuth;

@Component
public class UpdateAuthAction implements ResponseAction {

    private final TdlibAuthService authService;

    public UpdateAuthAction(TdlibAuthService authService) {
        this.authService = authService;
    }

    @Override
    public int code() {
        return TdApi.UpdateAuthorizationState.CONSTRUCTOR;
    }

    @Override
    public void execute(TdApi.Object object) {
        var payload = (TdApi.UpdateAuthorizationState) object;
        var state = payload.authorizationState;
        if (state == null) {
            return;
        }

        switch (state.getConstructor()) {
            case TdApi.AuthorizationStateWaitTdlibParameters.CONSTRUCTOR ->
                authService.setAppParams();

            case TdApi.AuthorizationStateWaitPhoneNumber.CONSTRUCTOR ->
                authService.setState(TdlibAuth.WAIT_PHONE);

            case TdApi.AuthorizationStateWaitCode.CONSTRUCTOR ->
                authService.setState(TdlibAuth.WAIT_CODE);

            case TdApi.AuthorizationStateReady.CONSTRUCTOR ->
                authService.setState(TdlibAuth.COMPLETE);
        }
    }
}
