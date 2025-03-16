package ru.sewaiper.ciconia.client.tdlib.error;

public class ClientException extends RuntimeException {

    public ClientException(int code, String message) {
        super("Error code " + code + ": " + message);
    }
}
