package bgu.spl.net.srv;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PrivateMsg implements Msg {
    private int Opi=6;
    private String reciever;
    private String PM;
    private String sender=null;//set after

    public PrivateMsg (ConcurrentLinkedQueue<String> list){
        if (list.size()<2){
            System.out.println ("Bad list for PM msg");
        }
        this.reciever=list.poll();
        this.PM=list.poll();
    }

    public String getReciever() {
        return reciever;
    }

    public String getPM() {
        return PM;
    }

    public String getSender() {
        return sender;
    }
    public void setsender (String sender){
        this.sender=sender;
    }
    public void PrintContent (){
        System.out.println("==Message Content:Private Message==");
        System.out.println("*OPI: "+Opi);
        System.out.println ("*PM text: "+PM);
        System.out.println ("*Reciever: "+reciever);
        System.out.println("==End of content!==");
    }
}
