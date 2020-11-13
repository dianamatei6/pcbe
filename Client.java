package p1_pcbe;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;


public class Client implements Runnable{
    private static HashMap<Integer, Client> map;
    private int idClient;
    private ReentrantLock lock= new ReentrantLock();
    
    public Client(int id){
        this.idClient = id;
        map.put(id,this);
    }
    
    @Override
    public void run(){
        while(true){
        	try {
                sendMsg();
        	}catch(InterruptedException e) {}

            //delay between exchanges of messages
            try{
                TimeUnit.SECONDS.sleep(1);
            }catch (Exception e){}
            
            Random rd = new Random();
            if(rd.nextBoolean())     //some clients will public a Topic randomly 
            	publicTopic();
            
            if(rd.nextBoolean())     //some clients will be randomly interested in some topics
            	askToSeeTopic("Topic");
        }
    }

    public Message createMessage(int receiverID, String text){
        Message msg = new Message(receiverID, text);
        return msg;
    }

    public void sendMsg() throws InterruptedException{
    	//creation of a message with a random receiver that is going to be sent
        int randomIdClient = (int)(Math.random() * 3) + 1;
        Message msg = createMessage(randomIdClient, "Hello ");
        
        Server.maximumCapacityQueue.acquire();
        Server.availabilityQueue.acquire();
        Server.queue.add(msg);
        Server.availabilityQueue.release();
        Server.fillQueue.release();
    }

    public void receiveMessage(Message msgRec){
        //receive message and print it together with the current time
        LocalTime currentTime = LocalTime.now();
        System.out.print("In client " + Integer.toString(idClient)+ ": " + msgRec.getText() + "from " + msgRec.getRec() + " at time: " + currentTime + " \n");
    }
    
    public Topic createTopic(String sbj, String txt ,long avbt) {
    	Topic tp = new Topic(sbj, txt, avbt);
    	return tp;
    }
    
    public void publicTopic() //it's adding topics to the list in a synchronized manner 
    {
    	//creation of a random topic
    	long randomAvbTime = (long)(Math.random() * 3000001) + 600000;
    	Topic tp = createTopic("Topic","Topic text",randomAvbTime);
    	
    	lock.lock();
    	try {
    		Server.topics.add(tp);
    	} finally {
    		lock.unlock();
    	}
    	
    }
    public void askToSeeTopic(String sbj) 
    {
    	while(!Server.permissionToRead)
    	{
    		try {
    			wait();
    		}catch(InterruptedException e) {}
    	}
        tryReadingTopic(sbj);
        Server.permissionToRead=false;
    	notifyAll();
    		
    }
    public void tryReadingTopic(String sbj)
    {
        for (Topic topic : Server.topics) {
    		        if (topic.getSubject().equals(sbj)) {
    		        	  System.out.print("Client "+ Integer.toString(idClient)+"found the topic with message "+topic.getText()+ "\n\n");
    		        	  return;
    		        }
    		    }
        System.out.print("Client "+ Integer.toString(idClient) +" didn't find desired topic.\n\n");
    }
    public static HashMap<Integer, Client> getMap()
    {
    	return map;
    }
}