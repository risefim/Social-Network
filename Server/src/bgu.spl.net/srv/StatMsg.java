package bgu.spl.net.srv;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;

public class StatMsg implements Msg {
    int Opi=8;
    private String username;

    public StatMsg (ConcurrentLinkedQueue<String> list) {
        username=list.poll();
        }
        public String getUsername(){
            return this.username;
        }
    public void PrintContent (){
        System.out.println("==Message Content:Stats==");
        System.out.println("*OPI: "+Opi);
        System.out.println ("*Username: "+username);
        System.out.println("==End of content!==");
    }
    }
