package Message;

public class Message {
    String sentence;
    String type;

    public Message (String sentence) {
        this.sentence = sentence;
        read();
    }

    public void read() {
        String[] messageComponents = sentence.split(" ");
    }
}
