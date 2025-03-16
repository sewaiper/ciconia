package ru.sewaiper.ciconia.repository;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import ru.sewaiper.ciconia.model.Channel;

import java.util.Optional;

public interface ChannelRepository extends CrudRepository<Channel, Long> {

    @Modifying
    @Query("""
            insert into channels (id, supergroup_id, name, title)
                values (:id, :supergroupId, :name, :title)
            on conflict (id) do nothing
            """)
    void insert(long id, long supergroupId, String name, String title);

    @Modifying
    @Query("""
            insert into subscriptions(user_id, channel_id)
                values(:userId, :channelId)
            on conflict (user_id, channel_id) do nothing
            """)
    void subscribe(long userId, long channelId);

    @Query("""
            select * from channels s where s.name = :name
            """)
    Optional<Channel> findChannel(String name);
}
