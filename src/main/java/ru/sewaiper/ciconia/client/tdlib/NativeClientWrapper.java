package ru.sewaiper.ciconia.client.tdlib;

import org.drinkless.tdlib.Client;
import org.drinkless.tdlib.TdApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.sewaiper.ciconia.client.tdlib.domain.TdlibRequest;

import java.io.IOError;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class NativeClientWrapper {

    private static final Logger LOG = LoggerFactory.getLogger(NativeClientWrapper.class);

    private final Map<Integer, ResponseAction> actions = new HashMap<>();

    private Client client;

    public NativeClientWrapper(List<ResponseAction> actions) {
        try {
            Client.execute(new TdApi.SetLogVerbosityLevel(0));
            Client.execute(new TdApi.SetLogStream(new TdApi.LogStreamFile("tdlib.log", 1 << 27, false)));
        } catch (Client.ExecutionException error) {
            throw new IOError(new IOException("Write access to the current directory is required"));
        }

        actions.forEach(action -> this.actions.put(action.code(), action));
    }

    @EventListener(ContextRefreshedEvent.class)
    public void onContextRefreshed() {
        this.client = Client.create(
                this::onResult,
                null, null
        );
    }

    public Client getOrigin() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    private void onResult(TdApi.Object object) {
        if (object == null) {
            LOG.warn("Receive is NULL event");
            return;
        }

        try {
            Optional.of(object.getConstructor())
                    .map(actions::get)
                    .ifPresent(action -> action.execute(object));
        } catch (Exception e) {
            LOG.error("Unable process event {}", object.getClass().getSimpleName(), e);
        }
    }

    public void sendRequest(TdlibRequest request) {
        if (client == null) {
            throw new IllegalStateException("TDLib client is missing");
        }
        client.send(request.function(), request.handler(), null);
    }
}
