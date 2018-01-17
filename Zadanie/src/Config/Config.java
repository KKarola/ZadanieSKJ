package Config;

public enum Config {
    INSTANCE;

    private int port = 10000;
    private String ip = "127.0.0.1";
    private int sizeOfPacket = 1024;
    private String path = "D://TORrent_";

    public int getPort () {
        return port;
    }

    public String getIp() {
        return ip;
    }

    public int getSizeOfPacket() {
        return sizeOfPacket;
    }

    public String getPath() {
        return path;
    }
}
