
public class Window {
    public int size; // Byte
    public int segmentSize; // 每个数据段的大小，认为就是MSS的值
    public Packet[] packets;
    public void initPackets() {
        packets = new Packet[size / segmentSize]; // 先设置好size再初始化packets
        for (Packet item : packets)
            item = null;
    }
    /**
     * 检查window数据包是否发送完毕
     * */
    public boolean ifFinished() {
        //用于判断是否可以装填数据包
        //null等效于ACK
        for (Packet packet : packets) if (packet != null && !packet.isAck) return false;
        return true;
    }
    /**
     * 寻找packets中已被应答的报文并替换为参数报文packet
     * @param packet 替换的报文
     * */
    public void replace(Packet packet) {
        for (Packet item : packets) {
            if (item == null || item.isAck) item = packet;
        }
    }
    //todo other possible methods
}

