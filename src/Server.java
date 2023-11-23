package src;

import java.io.*;
import java.net.*;
import java.util.Random;

public class Server extends Client{
    public int MSS = 0; // 报文可承载的最大数据量
    private boolean up = false; //标记服务端是否已启用
    private int listenPort = -1; //监听端口
    private boolean connected = false;

    public Window window = new Window(); //创建一个窗口，在sever中维护这个window

    private Packet receivedPacket;

    /**
     * 根据当前报文中window值设置window大小
     * */
    public void setWindowSize() {
        //todo
    }
    /**
     * 启用服务器
     * */
    public void start() {
        up = true;
    }
    /**
     * 关闭服务器
     * */
    public void close() {
        up = false;
    }
    /***
     * 对服务器状态作检查，如是否处于启用状态，监听端口是否为合法端口（-1即不合法端口）等
     * @return boolean
     */
    public boolean check() {
        return up && listenPort >= 0;
    }

    /**
     * 监听某个端口
     * @param port
     * */
    public void listen(int port) {
        listenPort = port;
    }

    public Server() {
        super();
        // todo
    }

    //TODO 其余需要实现的但该代码中未考虑到的部分

    public static void main(String[] argv) throws Exception{
        //argv[0]为服务器启动时监听的端口号; argv[1] argv[2]分别为传输文件的path和MSS(需要判断是否存在)
        //todo
        Server server = new Server();

        if (argv.length != 3) {
            System.out.println("Parameter count error!");
            return;
        }// 参数数量是否合法

        try {
            server.listen(Integer.parseInt(argv[0]));
            if (server.listenPort < 0 || server.listenPort > 65535) {
                System.out.println("Invalid port.");
                return;
            }
        }catch (NumberFormatException e) {
            System.out.println("Invalid port.");
            return;
        }// 端口号是否合法

        File file = new File(argv[1]);
        try (FileInputStream inputFromFile = new FileInputStream(file)){
            ;
        }catch (FileNotFoundException e) {
            System.out.println("File not found.");
            return;
        }// 文件是否存在

        try {
            server.MSS = Integer.parseInt(argv[2]);
            if (server.MSS > 1500 || server.MSS <= 0) {
                System.out.println("Invalid MSS.");
                return;
            }
        }catch (NumberFormatException e) {
            System.out.println("Invalid MSS.");
            return;
        }// MSS是否合法
        server.start();

        while (server.check()) {
            //todo 执行服务器逻辑
            //byte[] d = new byte[114];
            while (!server.connected) {
                //监听以接收报文
                ServerSocket welcomeSocket = new ServerSocket(server.listenPort);
                Socket listenSocket = welcomeSocket.accept();// 先主动建立一个用于接收初始请求报文的socket，只用于建立连接前的冷启动
                InputStream bytesFromClient = listenSocket.getInputStream();// 获取client的字节流
                OutputStream bytesToClient = listenSocket.getOutputStream();
                byte[] buffer = server.getBytes(bytesFromClient, 20);// buffer中存放client发来报文的字节形式
                Packet firstHandShake = server.buildPacket(buffer);// 组装报文
                server.receive(firstHandShake);// 收到client的第一次握手报文

                if (server.checkSYN(firstHandShake) && !server.checkACK(firstHandShake)) {
                    //应答，作为第二次握手的报文
                    int seq = new Random().nextInt(1234567890);// 该报文第一个字节id
                    byte[] id = Transformer.toBytes(seq, 4);
                    int ack = Transformer.toInteger(server.receivedPacket.getId()) + 1;// 该报文ack应为上一个报文id + 1
                    byte[] Ack = Transformer.toBytes(ack, 4);
                    byte[] spcBytes = new byte[2];
                    spcBytes[0] = server.receivedPacket.getSpcBytes()[0];
                    spcBytes[1] = (byte) (server.receivedPacket.getSpcBytes()[1] | (1 << 4));// ACK置1
                    int checkSum = 0;// todo calculate checkSum
                    Packet secondShakeHand = new Packet(
                            server.receivedPacket.getDest(), server.receivedPacket.getSrc(), id, Ack,
                            spcBytes, server.receivedPacket.getWindow(), Transformer.toBytes(checkSum, 2),
                            server.receivedPacket.getUrgent(), server.receivedPacket.getOptions(),
                            server.receivedPacket.getAlign(), server.receivedPacket.MSS
                    );
                    bytesToClient.write(secondShakeHand.getBytes());// 将报文发送给client，完成应答

                    //检查第三次握手报文
                    bytesFromClient = listenSocket.getInputStream();
                    buffer = server.getBytes(bytesFromClient, 20);
                    Packet thirdHandShake = server.buildPacket(buffer);
                    server.receive(thirdHandShake);
                    int clintSeq = Transformer.toInteger(server.receivedPacket.getId()), clientAck = -1;
                    if (server.checkACK(thirdHandShake)) {
                        clientAck = Transformer.toInteger(server.receivedPacket.getAck());
                    }else {
                        System.out.println("Failed to get connected.");
                        continue;
                    }
                    if (!server.checkSYN(thirdHandShake) && clintSeq == ack && clientAck == seq + 1) {
                        // 符合第三次握手的报文规范，可以建立连接
                        server.connected = true;
                        System.out.println("Connected to Client.");
                        server.setWindowSize();
                    }else {
                        System.out.println("Failed to get connected.");
                        //告诉client连接失败，send RST=1
                        spcBytes[1] = (byte) (spcBytes[1] | (1 << 2));// RST置1
                        checkSum = 1;// todo calculate checkSum
                        Packet failure = new Packet(
                            server.receivedPacket.getDest(), server.receivedPacket.getSrc(), id, Ack,
                                spcBytes, server.receivedPacket.getWindow(), Transformer.toBytes(checkSum, 2),
                                server.receivedPacket.getUrgent(), server.receivedPacket.getOptions(),
                                server.receivedPacket.getAlign(), server.MSS
                        );
                        bytesToClient.write(failure.getBytes()); // 通知client连接失败
                    }
                }
                else continue;// 对于未建立连接时的一切SYN != 1 || ACK != 0的报文直接丢弃
            }
            //todo Data Transfer
        }
        server.close();
        System.out.println("Server shut down unexpectedly.");
    }
}


