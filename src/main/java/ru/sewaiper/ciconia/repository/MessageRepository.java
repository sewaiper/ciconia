package ru.sewaiper.ciconia.repository;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import ru.sewaiper.ciconia.model.Message;

public interface MessageRepository extends CrudRepository<Message, Long> {

    @Modifying
    @Query("""
            insert into messages(id, channel_id, link, brief, "text", tag)
                values (:id, :channelId, :link, :brief, :text, :tag)
            """)
    void insert(long id, long channelId,
                String link, String brief,
                String text, Integer tag);
}
