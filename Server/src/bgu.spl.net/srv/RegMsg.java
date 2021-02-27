package bgu.spl.net.srv;

import java.util.concurrent.ConcurrentLinkedQueue;

public class RegMsg implements Msg{

    private int Opi=1;
    private String username;
    private String password;

    public RegMsg (ConcurrentLinkedQueue<String> list){
        if (list.size()<2){
            System.out.println ("Bad list for reg msg");
        }
        username=list.poll();
        password=list.poll();
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void PrintContent (){
        System.out.println("==Message Content:Register==");
        System.out.println("*OPI: "+Opi);
        System.out.println("*Username: "+username);
        System.out.println("*Password: "+password);
        System.out.println("==End of content!==");
    }
}
