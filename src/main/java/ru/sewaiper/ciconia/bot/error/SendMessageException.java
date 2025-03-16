package ru.sewaiper.ciconia.bot.error;

public class SendMessageException extends RuntimeException {

    public SendMessageException(String message, Throwable cause) {
        super(message, cause);
    }
}
