package p1_pcbe;

import java.util.*;
import java.util.concurrent.Semaphore;

public final class Server implements Runnable{
	private static Server instance;
	private long durationMessagesinServerMillisec= 3600000;
    public static Queue<Message> queue= new LinkedList<Message>();
    public static Collection<Topic> topics = Collections.synchronizedCollection(new ArrayList<>()); //protected list
    public static Semaphore maximumCapacityQueue= new Semaphore(10);
    public static Semaphore fillQueue= new Semaphore(0);
    public static Semaphore availabilityQueue= new Semaphore(1);
    public static boolean permissionToRead=false;

    private Server(){}
    public synchronized static Server getInstance() {
        if (instance == null) {
            instance = new Server();
        }
        return instance;
    }

    @Override
    public void run(){
        while(true){
        	try
        	{
        		handleMessages();
        	}catch(InterruptedException e) {}
            handleTopics();
            checkAvailabilityQueue();
            checkAvailabilityTopics();
        }
    }
   //handles the messages that were added to queue
    public void handleMessages()throws InterruptedException {
      while (true)
      {
    	fillQueue.acquire();
        availabilityQueue.acquire();
        Message gotMsg = queue.poll();  //poll returns first element on queue and erases it (after message "is read" it is deleted)
        availabilityQueue.release();
        maximumCapacityQueue.release();
        int idReceiver = gotMsg.getRec();
        Client c = Client.getMap().get(idReceiver);
        c.receiveMessage(gotMsg);       //receiver client reads the message 
      }
    }
    
    public synchronized void handleTopics(){
         while(permissionToRead) //wait to receive a request for permission to read the topic list
         {
     		try {
    			wait();
    		}catch(InterruptedException e) {}
        }
        permissionToRead=true;
        notifyAll();

    }
    public void checkAvailabilityQueue()
    {
        for (Message msg : queue) {
	        if (System.currentTimeMillis()-msg.getTime()>durationMessagesinServerMillisec) {
	        	  queue.remove(msg);
	        }
	    }
    }
    public void checkAvailabilityTopics()
    {
        for (Topic topic : topics) {
	        if (System.currentTimeMillis()-topic.getTime()>topic.getAvailabilityTime() || System.currentTimeMillis()-topic.getTime()>durationMessagesinServerMillisec) 
	            topics.remove(topic);
	    }
    }
}