package ru.sewaiper.ciconia.repository;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import ru.sewaiper.ciconia.model.User;

public interface UserRepository extends CrudRepository<User, Long> {

    @Modifying
    @Query("""
            insert into users(id, username, first_name, last_name, command, chat_id)
                values (:id, :username, :firstName, :lastName, :command, :chatId)
            on conflict (id) do nothing
            """)
    void insert(long id, String username,
                String firstName, String lastName,
                String command, long chatId);

    @Modifying
    @Query("""
            update users set command = :command where id = :id
            """)
    void updateCommand(long id, String command);
}
