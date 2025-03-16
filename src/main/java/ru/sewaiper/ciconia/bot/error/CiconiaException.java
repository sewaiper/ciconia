package ru.sewaiper.ciconia.bot.error;

public class CiconiaException extends RuntimeException {

    public CiconiaException(String message) {
        super(message);
    }

    public CiconiaException(String message, Throwable e) {
        super(message, e);
    }
}
