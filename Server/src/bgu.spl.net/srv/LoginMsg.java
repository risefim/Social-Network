package bgu.spl.net.srv;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;

public class LoginMsg implements Msg {
    private int Opi=2;
    private String username;
    private String password;

    public LoginMsg (ConcurrentLinkedQueue<String>list){
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
        System.out.println("==Message Content:Login==");
        System.out.println("*OPI: "+Opi);
        System.out.println("*Username: "+username);
        System.out.println("*Password: "+password);
        System.out.println("==End of content!==");
    }
}
