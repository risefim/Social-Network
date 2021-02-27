package bgu.spl.net.srv;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class User {
    private String username;
    private String password;
    private boolean isOnline=false;//offline by default
    private int connectionId=-1;
    private ConcurrentLinkedQueue<String> SentPosts=new ConcurrentLinkedQueue<>();//The key is message itself
    private ConcurrentHashMap<String,ConcurrentLinkedQueue<PostMsg>>UnreadedPosts=new ConcurrentHashMap<>();
    private ConcurrentLinkedQueue<String>SentPms=new ConcurrentLinkedQueue<>();
    private ConcurrentHashMap<String,ConcurrentLinkedQueue<PrivateMsg>>UnreadedPms=new ConcurrentHashMap<>();
    private ConcurrentLinkedQueue<String>Followers=new ConcurrentLinkedQueue<>();
    private ConcurrentLinkedQueue<String>Following=new ConcurrentLinkedQueue<>();
    private ConcurrentLinkedQueue<NotificationMsg>toNotify=new ConcurrentLinkedQueue<>();
    //-------------------------------------------------------------------------------------------
    public User (String username,String password){
        this.username=username;
        this.password=password;
    }
    //--------------------Additional methods----------------------

    public void isOnline(int connectionId){
        this.connectionId=connectionId;//only one CH can be logged on each user,if not logged set -1 value
        this.isOnline=true;
    }
    public void isOffline(){
        this.connectionId=-1;
        this.isOnline=false;}

    public void addPost(String post){
        this.getSentPosts().add(post);
    }
    public void addPm(String pm){
        SentPms.add(pm);}
    public void addNotification(NotificationMsg msg){
        toNotify.add(msg);
        System.out.println("len of notification list now is: "+toNotify.size());
        System.out.println("Notification added successfully!");
    }
    public ConcurrentLinkedQueue<NotificationMsg> getNotifications(){
        return this.toNotify;
    }
//    public LinkedList<NotificationMsg> CheckUnreaded(){//CheckUnreaded msg's and return fit notifications
//        Iterator<String> it=UnreadedPosts.keySet().iterator();//check
//        LinkedList<NotificationMsg>list=new LinkedList<>();
//        while (it.hasNext()){
//            String cur=it.next();
//            while(UnreadedPosts.get(cur).peek()!=null){
//                PostMsg post=UnreadedPosts.get(cur).poll();
//                NotificationMsg notify=new NotificationMsg(1,post.getPost(),post.getSender());
//                list.add(notify);//add to notifications list
//            }
//        }//Unreaded posts
//        it=UnreadedPms.keySet().iterator();
//        while (it.hasNext()){
//            String cur=it.next();
//            while(UnreadedPms.get(cur).peek()!=null){//transfer pms to 'readed'
//                PrivateMsg pm=UnreadedPms.get(cur).poll();
//                NotificationMsg notify=new NotificationMsg(0,pm.getPM(),pm.getSender());
//                list.add(notify);//add to notifications list
//            }
//        }//Unreaded pms
//        System.out.println ("Troubles-Unreaded msgs");
//        return null;
//    }
    //---------------------------------------------------------------

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setdConnectionId(int id) {
        this.connectionId=id;
    }

    public ConcurrentLinkedQueue<String> getFollowers() {
        return Followers;
    }

    public void setFollowers(ConcurrentLinkedQueue<String> followers) {
        Followers = followers;
    }

    public ConcurrentLinkedQueue<String> getFollowing() {
        return Following;
    }

    public void setFollowing(ConcurrentLinkedQueue<String> following) {
        Following = following;
    }
    public boolean getStatus() {
        return isOnline;
    }
    public int getConnectionId() {
        return connectionId;
    }

    public LinkedList<String>getStats(){
        LinkedList<String>list=new LinkedList<>();
        list.add(""+SentPosts.size());//string of no. of posts
        list.add(""+Followers.size());
        list.add(""+Following.size());
        return list;
    }

    public ConcurrentLinkedQueue<String> getSentPosts() {
        return SentPosts;
    }

    public ConcurrentLinkedQueue<String> getSentPms() {
        return SentPms;
    }

    public int SendNotification (NotificationMsg tonotify){
        if (isOnline){
            System.out.println ("Next condition...User is Online with ConID of:"+connectionId);
            return connectionId;
        }
        addNotification(tonotify);//added to awaiting notifications
        return -1;
    }

    public ConcurrentLinkedQueue<NotificationMsg> getUnreaded(){return toNotify;}

    public NotificationMsg checkUnreaded(){
        System.out.println("len of notification list was: "+toNotify.size()+" (User class)");
        NotificationMsg output=toNotify.poll();
        System.out.println("len of notification list now is(User class): "+toNotify.size());
        return output;

    }
    public boolean PrivateMsg (){
        return isOnline;
    }
}
