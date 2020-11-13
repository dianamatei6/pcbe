package p1_pcbe;

public class Message {

    private int receiver_id;
    private String message;
    private long timeOfCreationMilisec;

    public Message(int rec, String msg){
        this.receiver_id = rec;
        this.message = msg;
        timeOfCreationMilisec= System.currentTimeMillis();
    }

    public int getRec(){
        return receiver_id;
    }
    public String getText(){
        return message;
    }
    public long getTime(){
    	return timeOfCreationMilisec;
    }
}