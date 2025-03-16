package ru.sewaiper.ciconia.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import ru.sewaiper.ciconia.bot.command.CommandType;

@Table("users")
public class User {

    @Id
    private long id;

    private String username;

    private String firstName;

    private String lastName;

    private CommandType command;

    private long chatId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public CommandType getCommand() {
        return command;
    }

    public void setCommand(CommandType command) {
        this.command = command;
    }

    public long getChatId() {
        return chatId;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }
}
