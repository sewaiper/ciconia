package ru.sewaiper.ciconia.model;

@SuppressWarnings("unused")
public class LastPacket extends Packet {

    private int size;

    public LastPacket() {
    }

    public LastPacket(Packet packet) {
        setId(packet.getId());
        setUserId(packet.getUserId());
        setCreatedAt(packet.getCreatedAt());

        this.size = 0;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
