package ru.sewaiper.ciconia.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("channels")
public class Channel {

    @Id
    private long id;
    private long supergroupId;
    private String name;
    private String title;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getSupergroupId() {
        return supergroupId;
    }

    public void setSupergroupId(long supergroupId) {
        this.supergroupId = supergroupId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
