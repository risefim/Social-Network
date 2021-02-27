package bgu.spl.net.api.bidi;



import bgu.spl.net.api.bidi.BidiMessagingProtocol;
import bgu.spl.net.api.bidi.ConnectionsImpl;
import bgu.spl.net.srv.AckMsg;
import bgu.spl.net.srv.ErrorMsg;
import bgu.spl.net.srv.FollowUnMsg;
import bgu.spl.net.srv.LoginMsg;
import bgu.spl.net.srv.LogoutMsg;
import bgu.spl.net.srv.Msg;
import bgu.spl.net.srv.NotificationMsg;
import bgu.spl.net.srv.PostMsg;
import bgu.spl.net.srv.PrivateMsg;
import bgu.spl.net.srv.RegMsg;
import bgu.spl.net.srv.SharedDB;
import bgu.spl.net.srv.StatMsg;
import bgu.spl.net.srv.UserListMsg;

import java.text.Bidi;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;


public class OurMessagingProtocol implements BidiMessagingProtocol<Msg> {

    protected SharedDB BidiDB;//or should be protected
    private boolean shouldTerminate=false;
    protected Connections<Msg> Connections;
    protected int connectionid=0;
    protected String username=null;//We reach Our name after logging in so it can be logged in sign
	protected Object reglock=new Object();



    //-----------------------------------
    public OurMessagingProtocol (SharedDB DB){
        this.BidiDB=DB;
    }
    //----------------------------------------------------------------------------------

    public void start(int connectionId, Connections<Msg> connections) {
        this.Connections=connections;
        this.connectionid=connectionId;
    }

    //---------------------------------------------------------------------------------------------------------
    public void process(Msg message) {
        boolean result;
        System.out.println ("We reached OurMessagingProtocol");
        if (message instanceof RegMsg){
            System.out.println ("Case I;Register message: ");
             result=(username==null)&&Register(((RegMsg)message).getUsername(),((RegMsg)message).getPassword());
            if (result){
                System.out.println ("User added successfully!");
                SendAckMsg(1,null);
            }

            else{
                System.out.println ("Error adding user!");
                SendErrorMsg(1);}
        }
        if (message instanceof LoginMsg){
            System.out.println ("Case II;Login message: ");
            result=(username==null)&&Login(((LoginMsg)message).getUsername(),((LoginMsg)message).getPassword(),connectionid);
            if (result){
                username=((LoginMsg)message).getUsername();
                System.out.println ("Logged in!");
                SendAckMsg(2,null);
            }
            else{SendErrorMsg(2);}
        }
        if (message instanceof LogoutMsg){
            System.out.println ("Case III;Logout message: ");
            result=(username!=null)&&Logout(username);
            if (result){
                SendAckMsg(3,null);
            }
            else{SendErrorMsg(3);}
        }
        if (message instanceof FollowUnMsg){
            System.out.println ("Case IV;Follow/Unfollow message: ");
            if (username!=null){
                ((FollowUnMsg)message).setSender(username);
                System.out.println ("Sender name is: "+((FollowUnMsg)message).getSender());
                LinkedList<String>list=FollowUnfollow((FollowUnMsg) message);
                result=list.size()!=0;
                if (result){
                    System.out.println ("We succeed to follow next num of users: "+list.size());
                    SendAckMsg(4,list);
                }
                else{
                    SendErrorMsg(4);
                }
            }
            else{SendErrorMsg(4);}

            }
        if (message instanceof PostMsg){
            System.out.println ("Case V: Post message: ");
            if (username!=null){
                ((PostMsg) message).setsender(username);
                Post((PostMsg) message);
                SendAckMsg(5,null);
            }
            else{
                SendErrorMsg(5);
            }
        }
        if (message instanceof PrivateMsg){
            System.out.println ("Case VI: PM message: ");
            System.out.println ("(protocol)Checking conditions...Sender Online: "+username!=null);
            if (username!=null&&BidiDB.DBsearch(((PrivateMsg)message).getReciever()))
                {
                System.out.println("(protocol):proceeding....");
                PrivateMsg((PrivateMsg) message);
                SendAckMsg(6,null);
                 }
            else
                {
                SendErrorMsg(6);
            }
        }
        if (message instanceof UserListMsg){
            System.out.println ("Case VII: UserList message: ");
            if (username!=null)
            {
                ((UserListMsg) message).setSender(username);//necessary??
                LinkedList<String>list=null;
               list=Userlist();
              SendAckMsg(7,list);
            }
            else
            {
                SendErrorMsg(7);
            }
        }
        if (message instanceof StatMsg){
            System.out.println ("Case VIII: Stats message: ");
            if (username!=null&&BidiDB.DBsearch(((StatMsg)message).getUsername()))
            {
                LinkedList<String>list=BidiDB.getUserStats(((StatMsg) message).getUsername());
                System.out.println("list len is: "+list.size());
                SendAckMsg(8,list);
            }
            else
            {
                SendErrorMsg(8);
            }
        }
        }
    //-----------------------------------------------------------------------------------
    private boolean Register (String username,String password){
        synchronized (reglock){return BidiDB.addUser(username,password);}
    }
    private boolean Login (String username,String password,int connectionid){
        if (BidiDB.setOnline(username,password,connectionid)){
            checkUnreaded(username);//in case of successful connection check unreaded msgs
            return true;
        }
        return false;
    }
    private boolean Logout (String username){
       return (BidiDB.setOffline(username));
    }
    //---------------------------------------------------------------------------------------
    private LinkedList<String> FollowUnfollow (FollowUnMsg message){
    LinkedList<String> list=new LinkedList<>();
        if (message.getUsersNum()!=0){
            int sign=message.getSign();
            for (String cur:message.getUserlist()){
                if (sign==0){//follow cur
                    if (BidiDB.Follow(message.getSender(),cur)){
                        list.add(cur);
                    }
                }
                if (sign==1){//unfollow cur
                    if (BidiDB.Unfollow(message.getSender(),cur)){
                        list.add(cur);
                    }
                }
            }
        }
        return list;
    }

    public void Post(PostMsg message){
        if (BidiDB.getUsers().containsKey(message.getSender())){
            BidiDB.getUsers().get(username).addPost(message.getPost());//add to poster msg's
            LinkedList<String>taggedUsers=message.gettagged();
            LinkedList<String>reserve=new LinkedList<>();//Dirty solution of double list
            //===============Part I: @tagged users@=========================
            System.out.println("Checking tags by DB:Length of list is: "+taggedUsers.size()+",/(1)");
            for (String tagged:taggedUsers){

                System.out.println("Current user to check from tagged list: "+tagged);
                System.out.println(tagged+" found in DB: "+(BidiDB.getUsers().get(tagged)!=null));
                if (BidiDB.getUsers().get(tagged)!=null&&!reserve.contains(tagged)){//tagged is legal
                    System.out.println("Next condition...");
                    if (!BidiDB.getUsers().get(username).getFollowers().contains(tagged)){
                        System.out.println("Next condition...tagged is not a follower");
                        NotificationMsg tonotify=new NotificationMsg((byte)1,message.getPost(),message.getSender());//tagged is not follower
                        if (BidiDB.getUsers().get(tagged).getStatus()){//tagged is online
                            System.out.println("Next condition...tagged is online now");
                            int result=BidiDB.SendNotification(tagged,tonotify);
                            if (result!=-1){
                                System.out.println("Next condition...User is not a follower-send notification right now!");
                                SendNotification(result,tonotify);
                            }
                        }
                        else{
                            System.out.println("Next condition...offliine,notify later");
                            BidiDB.toNotifyLater(tagged,tonotify);
                        }
                    }
                }
                reserve.add(tagged);
            }
            //================Part II:followers ================================
            System.out.println ("Next condition...checking followers");
            Iterator<String> it =BidiDB.getUsers().get(username).getFollowers().iterator();
            while (it.hasNext()){
                String cur=it.next();
                System.out.println(cur+" found in DB: "+BidiDB.getUsers().containsKey(cur));
                if (BidiDB.getUsers().get(cur)!=null)
                    {//current follower is legal
                    NotificationMsg msg=new NotificationMsg((byte)1,message.getPost(),message.getSender());
                    if (BidiDB.getUsers().get(cur).getStatus()){//current follower is online
                        //send notification right now
                        SendNotification(BidiDB.getConnectionIdByUsername(cur),msg);
                    }
                    else{

                        BidiDB.toNotifyLater(username,msg);
                    }
                }
            }
        }
    }

    public void  PrivateMsg(PrivateMsg message){
            boolean result=BidiDB.PrivateMsg(message.getReciever(),username,message);
            if (result) {
                NotificationMsg tosend = new NotificationMsg((byte) 0, message.getPM(), username);
                SendNotification(BidiDB.getConnectionIdByUsername((message).getReciever()),tosend);}
         }
    public LinkedList<String> Userlist (){
        System.out.println ("Building UserList...(protocol)");
        LinkedList<String>list=BidiDB.UserList();
        return list;
    }
    //--------------------------Errors,ACK's,Notifications----------------------------------
    public void SendAckMsg (int BackOpi,LinkedList<String>list){
        System.out.println ("Converting ACK response...");
        AckMsg output=new AckMsg (BackOpi,list);//create and send Ack msg
        this.Connections.send(connectionid,output);
        if (BackOpi==3){
            System.out.println ("Terminating...");
            Connections.disconnect(connectionid);
            shouldTerminate();
        }
    }
    public void SendErrorMsg (int BackOpi){//create and send Error msg
        ErrorMsg output=new ErrorMsg (BackOpi);
        this.Connections.send(this.connectionid,output);
    }
    public void SendNotification (int connectionid,NotificationMsg message){//Send quick notification
        System.out.println("Sending notification msg to next ConId: "+connectionid);
        message.PrintContent();
            Connections.send(connectionid,message);
    }

    //------------------------------------------------------------------------------------
    public void checkUnreaded(String username){
        System.out.println ("Checking unreaded msgs...");
        NotificationMsg cur=null;
        boolean flag=true;
        while (flag){
            System.out.println ("Checking unreaded msgs...Start checking(protocol)");
            cur=BidiDB.checkUnreaded(username);
            if (cur==null)
            {
                flag=false;
            }
            else {
                SendNotification(connectionid,cur);
            }
        }
    }
    //-------------------------------------------------------------------------------------
    @Override
    public boolean shouldTerminate() {//Meaning of command???
        return false;
    }
}

