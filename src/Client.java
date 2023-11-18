package src;

import java.net.*;

public class Client {

    public Packet receivedPacket;

    public void receive(Packet packet) {
        receivedPacket = packet;
    }

    /**
     * 检查FIN位，判断是否挥手释放连接
     * @param packet
     * @return boolean
     * */
    public boolean checkFIN(Packet packet) {
        //todo
        return false;
    }
    /**
     * 检查SYN位，判断是否握手建立连接
     * @param packet
     * @return boolean
     * */
    public boolean checkSYN(Packet packet) {
        //todo
        return false;
    }
    /**
     * 该方法发送报文
     * @param connectionSocket: 与客户端建立连结的服务端
     * @param packet 报文
     * */
    public void send(Socket connectionSocket, Packet packet) {
        //todo
    }
    /***
     *回显当前报文内容，即显示收到的信息
     *回显格式：src:... dest:... id:... ack:... window:... data:...
     */
    public void print() {
        //todo
    }
    /**
     * 把收到的字节流组装成报文, 字节流中一定包含首部，可能包含数据
     * @param fromClient: 从客户端收到的字节流
     * @return Packet: 组装后的报文
     * */
    public Packet buildPacket(byte[] fromClient) {
        //todo
        return null;
    }

    //TODO 其余需要实现的但该代码中未考虑到的部分
    public static void main(String[] argv) throws Exception{
        while (true) {
            //todo
        }
    }
}
