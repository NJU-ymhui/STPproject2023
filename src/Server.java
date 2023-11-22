package src;

public class Server extends Client{
    public int MSS = 0;
    private boolean up = false; //标记服务端是否已启用
    private int listenPort = -1; //监听端口
    private boolean connected = false;

    public Window window = new Window(); //创建一个窗口，在sever中维护这个window

    private Packet receivedPacket;
    public Packet getPacket() {
        return receivedPacket;
    }
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
        //todo
    }
    /**
     * 关闭服务器
     * */
    public void close() {
        //todo
    }
    /***
     * 对服务器状态作检查，如是否处于启用状态，监听端口是否为合法端口（-1即不合法端口）等
     * @return boolean
     */
    public boolean check() {
        //todo
        return true;
    }

    /**
     * 监听某个端口
     * @param port
     * */
    public void listen(int port) {
        //todo
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
        server.start();
        while (server.check()) {
            //执行服务器逻辑
            //todo
            byte[] d = new byte[114];
        }
        server.close();
        System.out.println("Server shut down unexpectedly.");
    }
}


