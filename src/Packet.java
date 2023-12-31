
public class Packet {
    private Data data = null;
    public int MSS = 1500;
    public int headSize;
    public boolean isAck = false; // 该报文是否已被确认
    private byte[] srcPort = new byte[2];
    private byte[] destPort = new byte[2];
    private byte[] id = new byte[4];
    private byte[] ack = new byte[4];
    private byte[] spcBytes = new byte[2];//储存数据偏移（0~3）保留（4~9）URG(10)ACK(11)PSH(12)RST(13)SYN(14)FIN(15)
    private byte[] Window = new byte[2];
    private byte[] check = new byte[2];
    private byte[] urgent = new byte[2];
    private byte[] options;//可选项
    private byte[] align;//填充
    public Packet(
            byte[] src, byte[] dest, byte[] id, byte[] ack, byte[] spcBytes,
            byte[] Window, byte[] check, byte[] urgent, byte[] options, byte[] align, int MSS) {
        this.srcPort = src;
        this.destPort = dest;
        this.id = id;
        this.ack = ack;
        this.spcBytes = spcBytes;
        this.Window = Window;
        this.check = check;
        this.urgent = urgent;
        this.options = options;
        this.align = align;
        this.MSS = MSS;
        headSize = 20 + options.length + align.length;
    }
    /**
     * 将报文转化为字节流传输
     * @return 字节流
     * */
    public byte[] getBytes() {
        int dataLen = 0;
        if (data != null && data.getData() != null) dataLen = data.getData().length;
        byte[] res = new byte[20 + options.length + align.length + dataLen];
        System.arraycopy(srcPort, 0, res, 0, 2);
        System.arraycopy(destPort,0, res, 2, 2);
        System.arraycopy(id, 0, res, 4, 4);
        System.arraycopy(ack, 0, res, 8, 4);
        System.arraycopy(spcBytes, 0, res, 12, 2);
        System.arraycopy(Window, 0, res, 14, 2);
        System.arraycopy(check, 0, res, 16, 2);
        System.arraycopy(urgent, 0, res, 18, 2);

        System.arraycopy(options, 0, res, 20, options.length);
        System.arraycopy(align, 0, res, 20 + options.length, align.length);

        if (dataLen > 0) {
            System.arraycopy(data.getData(), 0, res, 20 + options.length + align.length, dataLen);
        }
        return res;
    }
    public int getMSS() {
        return MSS;
    }
    public byte[] getSrc() {
        return srcPort;
    }
    public int getSrcPort() {
        return (int) srcPort[0] * (int) srcPort[1];
    }
    public byte[] getDest() {
        return destPort;
    }
    public int getDestPort() {
        return (int) destPort[0] * (int) destPort[1];
    }
    public byte[] getId() {
        return id;
    }
    public byte[] getAck() {
        return ack;
    }
    public byte[] getSpcBytes() { return spcBytes; }
    /**
     * @return 获得offset，也是首部长度
     * **/

    public int getOffset() {
        int offset = 0;
        for (int i = 0; i < 4; i++) {
            offset += ((spcBytes[0] >> i) & 1) * (1 << i);
        }
        return offset;
    }
    public int getReserve() {
        return 0;
    }
    public byte[] getWindow() {
        return Window;
    }
    public byte[] getCheck() {
        return check;
    }
    public byte[] getUrgent() {
        return urgent;
    }
    public byte[] getOptions() {
        return options;
    }
    public byte[] getAlign() {
        return align;
    }
    public void setData(Data data) {
        this.data = data;
    }
    public Data getData() {
        return data;
    }
    public boolean urgentValid() {
        return ((spcBytes[1] >> 2) & 1) == 1;
    }
    public boolean ackValid() {
        return ((spcBytes[1] >> 3) & 1) == 1;
    }
    public boolean pushNow() {
        return ((spcBytes[1] >> 4) & 1) == 1;
    }
    public boolean isReset() {
        return ((spcBytes[1] >> 5) & 1) == 1;
    }
    public boolean checkSYN() {
        return ((spcBytes[1] >> 6) & 1) == 1;
    }
    public boolean checkFIN() {
        return ((spcBytes[1] >> 7) & 1) == 1;
    }

}

