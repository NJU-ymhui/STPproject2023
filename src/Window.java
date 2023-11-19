package src;

public class Window {
    public int size; // Byte
    public int segmentSize; // 每个数据段的大小，认为就是MSS的值
    public Packet[] packets;
    public void initPackets() {
        packets = new Packet[size / segmentSize]; // 先设置好size再初始化packets
    }
    public boolean ifFinished() {
        for (Packet packet : packets) if (!packet.isAck) return false;
        return true;
    }
    //todo
}

