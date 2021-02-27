package bgu.spl.net.srv;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;





public class FollowUnMsg implements Msg {


    @SuppressWarnings("unckecked")
    private int Opi=4;
    private int sign=-1;//zero or 1
    private int usersNum=0;
    private LinkedList<String>userlist=new LinkedList<>();
    private String sender="";

    public FollowUnMsg (ConcurrentLinkedQueue<String>list){
       sign=Integer.parseInt(list.poll());
       usersNum=Integer.parseInt(list.poll());


        while (!list.isEmpty()){
            userlist.add(list.poll());
        }

    }

    public int getSign() {
        return sign;
    }

    public int getUsersNum() {
        return usersNum;
    }

    public void setSender (String sender){
        this.sender=sender;
    }

    public String getSender (){return sender;}

    public LinkedList<String> getUserlist() {
        return userlist;
    }
    public void PrintContent (){
        System.out.println("==Message Content:Follow/UnFollow==");
        System.out.println("*OPI: "+Opi);
        System.out.println ("*num of users on a list: "+usersNum);
        System.out.println("*Follow/UnFollow: "+sign);
        System.out.println("*UserList: ");
        for (String user:userlist){
            System.out.println ("*username: "+user);
        }
        System.out.println("==End of content!==");
    }
}
