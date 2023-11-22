package src;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.*;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Client {
    // Hello^_^, I'm Pchan.

    public Packet receivedPacket;
    public int MSS = 1500;

    private byte[] serverAddr = new byte[4];
    private int serverPort;

    public void receive(Packet packet) {
        receivedPacket = packet;
    }

    /**
     * 检查FIN位，判断是否挥手释放连接
     * @param packet
     * @return boolean
     * */
    public boolean checkFIN(Packet packet) {
        return packet.checkFIN();
    }
    /**
     * 检查SYN位，判断是否握手建立连接
     * @param packet
     * @return boolean
     * */
    public boolean checkSYN(Packet packet) {
        return packet.checkSYN();
    }
    /**
     * 该方法发送报文
     * @param connectionSocket: 与客户端建立连结的服务端
     * @param packet 报文
     * */
    public void send(Socket connectionSocket, Packet packet) throws IOException {
        OutputStream outToServer = connectionSocket.getOutputStream();
        DataOutputStream out = new DataOutputStream(outToServer);
        out.write(packet.getBytes());
    }
    /***
     *回显当前报文内容，即显示收到的信息
     *回显格式：src:... dest:... id:... ack:... window:... data:...
     */
    public void print() {
        System.out.printf("src:%d dest:%d id:%s ack:%s window:%s data:%s\n",
                receivedPacket.getSrcPort(),
                receivedPacket.getDestPort(),
                Arrays.toString(receivedPacket.getId()),
                Arrays.toString(receivedPacket.getBytes()),
                Arrays.toString(receivedPacket.getWindow()),
                Arrays.toString(receivedPacket.getData().getData()));
    }
    /**
     * 把收到的字节流组装成报文, 字节流中一定包含首部，可能包含数据
     * @param fromClient: 从客户端收到的字节流
     * @return Packet: 组装后的报文
     * */
    public Packet buildPacket(byte[] fromClient) {
        int headSize = ((int) fromClient[12] >> 4) * 4;
        return new Packet(Arrays.copyOfRange(fromClient, 0, 2),
                Arrays.copyOfRange(fromClient, 2, 4),
                Arrays.copyOfRange(fromClient, 4, 8),
                Arrays.copyOfRange(fromClient, 8, 12),
                Arrays.copyOfRange(fromClient, 12, 14),
                Arrays.copyOfRange(fromClient, 14, 16),
                Arrays.copyOfRange(fromClient, 16, 18),
                Arrays.copyOfRange(fromClient, 18, 20),
                Arrays.copyOfRange(fromClient, 20, fromClient.length),
                new byte[]{},
                MSS);
    }

    public Client(String addr, String port) {
        String addrPattern = "(\\d+)\\.(\\d+)\\.(\\d+)\\.(\\d+)";
        Pattern r = Pattern.compile(addrPattern);
        Matcher matcher = r.matcher(addr);
        if (matcher.find()) {
            serverAddr[0] = Byte.parseByte(matcher.group(1));
            serverAddr[1] = Byte.parseByte(matcher.group(2));
            serverAddr[2] = Byte.parseByte(matcher.group(3));
            serverAddr[3] = Byte.parseByte(matcher.group(4));
        } else {
            System.out.println("Wrong IP Address!");
        }
        serverPort = Byte.parseByte(port);
    }

    public Client() {

    }


    /** 可能存在的指令(暂定)：
     * send (filename)
     * get (filename)
     */
    protected String[] commands = new String[]{"send (.*)", "get (.*)"};
    public boolean run(String command) {
        for (String s : commands) {

        }
        return false;
    }


    //TODO 其余需要实现的但该代码中未考虑到的部分
    public static void main(String[] argv) throws Exception{
        Scanner scanner = new Scanner(System.in);
        System.out.println("Client started!");
        while (true) {
            System.out.println("Enter a port to connect:");
            String port = scanner.next();
            try {
                Client client = new Client("127.0.0.1", port);
                InetAddress address = InetAddress.getByAddress(client.serverAddr);
                Socket socket = new Socket(address, client.serverPort);
                // TODO 模拟三次握手过程
                System.out.println("Connection succeeded.");

                while (true) {
                    String command = scanner.nextLine();
                    if (command.equals("quit") || command.equals("exit")) {
                        // TODO 模拟四次挥手过程
                        break;
                    }
                    // TODO 处理各种指令
                }

                socket.close();
            } catch (IOException e) {
                System.out.println("Connection failed. Request time out...");
            }
        }
    }
}
