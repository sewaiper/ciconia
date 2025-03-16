package ru.sewaiper.ciconia.error;

public class InvalidPacketTTLException extends RuntimeException {

    public InvalidPacketTTLException(String message) {
        super(message);
    }
}
