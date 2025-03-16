package ru.sewaiper.ciconia.bot;

public record CiconiaMessageTo(long chatId,
                               Integer replyMessageId,
                               String text) {

    public CiconiaMessageTo(long chatId, String text) {
        this(chatId, null, text);
    }
}
