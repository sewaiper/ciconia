package ru.sewaiper.ciconia.repository.packet;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import ru.sewaiper.ciconia.model.LastPacket;
import ru.sewaiper.ciconia.model.Packet;
import ru.sewaiper.ciconia.model.PacketConfig;

import java.util.List;
import java.util.Optional;

public interface PacketRepository extends CrudRepository<Packet, Long>, CustomPacketRepository {

    @Modifying
    @Query("""
            insert into packet_config(id, max_size, ttl)
                values (:userId, :maxSize, :ttl)
            on conflict (id) do update set max_size = :maxSize,
                                           ttl = :ttl
            """)
    void updateConfig(long userId, int maxSize, int ttl);

    @Query("""
            select
                u.id as user_id,
                u.chat_id ,
                pc.max_size ,
                pc.ttl
            from subscriptions s
            inner join users u on s.user_id = u.id
            inner join packet_config pc on u.id = pc.id
            where s.channel_id = :channelId
            """)
    List<PacketConfig> findConfig(long channelId);

    @Query("""
            select
                p.*,
                count(pc.packet_id) as "size"
            from packets p
            left join packets_content pc on p.id = pc.packet_id
            where p.user_id = :userId
            group by p.id
            order by p.created_at desc limit 1
            """)
    Optional<LastPacket> findLast(long userId);

    @Modifying
    @Query("""
            insert into packets_content(packet_id, message_id)
                values (:packetId, :messageId)
            """)
    void insertContent(long packetId, long messageId);
}
