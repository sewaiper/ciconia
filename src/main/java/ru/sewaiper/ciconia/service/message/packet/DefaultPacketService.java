package ru.sewaiper.ciconia.service.message.packet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import ru.sewaiper.ciconia.error.InvalidPacketTTLException;
import ru.sewaiper.ciconia.model.LastPacket;
import ru.sewaiper.ciconia.model.Message;
import ru.sewaiper.ciconia.model.Packet;
import ru.sewaiper.ciconia.model.PacketConfig;
import ru.sewaiper.ciconia.repository.packet.PacketRepository;
import ru.sewaiper.ciconia.service.message.publish.PublishRequest;
import ru.sewaiper.ciconia.service.message.publish.PublishService;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@Service
public class DefaultPacketService implements PacketService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultPacketService.class);

    private final PublishService publishService;
    private final PacketRepository packetRepository;

    private final PlatformTransactionManager transactionManager;

    public DefaultPacketService(PublishService publishService,
                                PacketRepository packetRepository,
                                PlatformTransactionManager transactionManager) {

        this.publishService = publishService;
        this.packetRepository = packetRepository;
        this.transactionManager = transactionManager;
    }

    @Override
    @Transactional
    public void configurePacket(long userId, int maxSize, String ttl) {
        packetRepository.updateConfig(userId, maxSize, parseTTL(ttl));
    }

    private int parseTTL(String value) {
        var ttl = Duration.parse(value);

        if (ttl.toMinutes() < 1) {
            throw new InvalidPacketTTLException("TTL must be greater than 1 minute");
        }

        if (ttl.toHours() > 12) {
            throw new InvalidPacketTTLException("TTL must be less than 12 hours");
        }

        return (int) ttl.toMillis();
    }

    @Async("packetHandleExecutor")
    public void append(Message message) {
        LOG.info("Append message to packets");

        packetRepository.findConfig(message.getChannelId())
                .forEach(config -> packetTask(config, message));
    }

    private void packetTask(PacketConfig config, Message message) {
        try {
            //noinspection DataFlowIssue
            new TransactionTemplate(transactionManager)
                    .execute(status -> doProcess(config, message))
                    .ifPresent(publishService::print);
        } catch (Exception e) {
            LOG.error("Unable to process packet message {} for user {}",
                    message.getId(), config.getUserId(), e);
        }
    }

    @NonNull
    private Optional<PublishRequest> doProcess(PacketConfig config, Message message) {
        var userId = config.getUserId();

        var packet = packetRepository.findLast(userId)
                .orElseGet(() -> createNew(userId));

        packetRepository.insertContent(packet.getId(), message.getId());

        if (isPacketSwollen(config, packet) ||
                isPacketExpired(config, packet)) {

            createNew(packet.getUserId());

            var content = packetRepository.findContent(packet.getId());

            return Optional.of(
                    new PublishRequest(config.getChatId(), content)
            );
        }

        return Optional.empty();
    }

    private boolean isPacketSwollen(PacketConfig config, LastPacket packet) {
        return packet.getSize() + 1 >= config.getMaxSize();
    }

    private boolean isPacketExpired(PacketConfig config, LastPacket packet) {
        var now = Instant.now();
        var threshold = Instant.ofEpochMilli(packet.getCreatedAt())
                .plus(Duration.ofMillis(config.getTtl()));

        return now.isAfter(threshold);
    }

    private LastPacket createNew(long userId) {
        var entity = new Packet();
        entity.setUserId(userId);
        entity.setCreatedAt(Instant.now().toEpochMilli());
        return new LastPacket(packetRepository.save(entity));
    }
}
