package ru.sewaiper.ciconia.client.tdlib.error;

public class SubscribeException extends RuntimeException {

    public SubscribeException(String message) {
        super(message);
    }

    public SubscribeException(Throwable cause) {
        super(cause);
    }
}
