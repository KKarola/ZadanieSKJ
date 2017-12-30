package Server;

public class Message {

    String sentence;
    String type;
    String param;


    public Message(String sentence) {
        this.sentence = sentence;
        read();
    }

    private void read() {
        String[] messageComponents = this.sentence.split(" ");
        this.type = messageComponents[0];
        if(messageComponents.length > 1) {
            this.param = messageComponents[1];
        }
    }
}