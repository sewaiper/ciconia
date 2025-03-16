package ru.sewaiper.ciconia.model;

@SuppressWarnings("unused")
public class PacketConfig {

    /**
     * Идентификатор пользователя.
     */
    private long userId;

    /**
     * Идентификатор чата: <b>бот</b>-<b>пользователь</b>. Используется для отправки пакета подписчику.
     */
    private long chatId;

    /**
     * Максимальный размер пакета.
     */
    private int maxSize;

    /**
     * Время жизни пакета.
     */
    private int ttl;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getChatId() {
        return chatId;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public int getTtl() {
        return ttl;
    }

    public void setTtl(int ttl) {
        this.ttl = ttl;
    }
}
