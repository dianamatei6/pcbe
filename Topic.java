package p1_pcbe;

public class Topic {
	
    private String subject;
    private String text;
    private long availabilityTime; //in milliseconds 
    private long timeOfCreation;

    public Topic(String sbj, String txt ,long avbt){
        this.subject = sbj;
        this.text = txt;
        this.availabilityTime=avbt;
        timeOfCreation= System.currentTimeMillis();
    }

    public String getSubject(){
        return subject;
    }
    public String getText(){
        return text;
    }
    public long getAvailabilityTime(){
    	return availabilityTime;
    }
    public long getTime(){
    	return timeOfCreation;
    }
}
