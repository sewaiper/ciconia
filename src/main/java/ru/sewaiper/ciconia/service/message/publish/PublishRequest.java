package ru.sewaiper.ciconia.service.message.publish;

import ru.sewaiper.ciconia.model.PacketMessage;

import java.util.List;

public record PublishRequest(long readerId, List<PacketMessage> content) {
}
