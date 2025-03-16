package ru.sewaiper.ciconia.service.message.packet;

import ru.sewaiper.ciconia.model.Message;

public interface PacketService {

    void configurePacket(long userId, int maxSize, String ttl);

    void append(Message message);
}
