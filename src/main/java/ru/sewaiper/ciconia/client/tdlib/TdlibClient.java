package ru.sewaiper.ciconia.client.tdlib;

import org.drinkless.tdlib.Client;
import org.drinkless.tdlib.TdApi;
import org.springframework.context.annotation.Lazy;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import ru.sewaiper.ciconia.client.tdlib.domain.TdlibRequest;
import ru.sewaiper.ciconia.client.tdlib.error.ClientException;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class TdlibClient {

    private final NativeClientWrapper wrapper;

    public TdlibClient(@Lazy NativeClientWrapper client) {
        this.wrapper = client;
    }

    public <T extends TdApi.Object> T send(@NonNull TdApi.Function<T> payload) {
        var resultRef = new AtomicReference<TdApi.Object>(null);
        var latch = new CountDownLatch(1);

        try {
            sendAsync(payload, response -> {
                resultRef.set(response);
                latch.countDown();
            });
            latch.await();

            var result = resultRef.get();

            if (result == null ||
                    TdApi.Error.CONSTRUCTOR == result.getConstructor()) {

                var error = (TdApi.Error) result;
                throw new ClientException(error.code, error.message);
            }

            //noinspection unchecked
            return (T) result;
        } catch (InterruptedException e) {
            throw new IllegalStateException();
        }
    }

    public <T extends TdApi.Object> void sendAsync(@NonNull TdApi.Function<T> payload) {
        sendAsync(payload, null);
    }

    public <T extends TdApi.Object> void sendAsync(@NonNull TdApi.Function<T> payload,
                                                   Client.ResultHandler handler) {

        wrapper.sendRequest(new TdlibRequest(payload, handler));
    }
}
