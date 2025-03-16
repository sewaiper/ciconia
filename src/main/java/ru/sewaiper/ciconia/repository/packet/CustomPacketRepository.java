package ru.sewaiper.ciconia.repository.packet;

import ru.sewaiper.ciconia.model.PacketMessage;

import java.util.List;

public interface CustomPacketRepository {

    List<PacketMessage> findContent(long packetId);
}
