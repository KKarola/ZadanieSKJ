package Server;

public class Message {

    String sentence;
    public String type;
    String param;


    public Message (String sentence) {
        this.sentence = sentence;
        read();
    }

    private void read() {
        String[] messageComponents = sentence.split(" ");
        type = messageComponents[0];
        if(messageComponents.length > 1) {
            param = messageComponents[1];
        }
    }
}