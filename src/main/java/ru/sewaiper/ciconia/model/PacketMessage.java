package ru.sewaiper.ciconia.model;

import ru.sewaiper.ciconia.service.message.tag.CiconiaTag;

public class PacketMessage {

    private String link;
    private String brief;
    private CiconiaTag tag;

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }

    public CiconiaTag getTag() {
        return tag;
    }

    public void setTag(CiconiaTag tag) {
        this.tag = tag;
    }
}
