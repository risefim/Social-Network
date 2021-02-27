package bgu.spl.net.srv;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SharedDB {
    private ConcurrentHashMap<String,User> Users=new ConcurrentHashMap<>();
    private ConcurrentHashMap<String,Integer> ConnectionByUser=new ConcurrentHashMap<>();
    //-------------------------------------------------------------------------------------------------
    public SharedDB (){}
    //-------------------------Register,Login,Logout interactions--------------------------------------
    public boolean addUser (String username,String password){
        if (!Users.containsKey(username)){
            User toAdd=new User (username, password);
            Users.putIfAbsent(username,toAdd);
            System.out.println ("User "+username+" added Successfully to UserList(DB): "+Users.containsKey(username));
            ConnectionByUser.putIfAbsent(username,-1);
            return true;
        }
        return false;
    }
    //------------------------------ShortCuts---------------------------------------
    public boolean setOnline (String username,String password,int connectionId){
        if (Users.containsKey(username)&&(ConnectionByUser.get(username)==-1)&&
                Users.get(username).getPassword().equals(password) &&
                !Users.get(username).getStatus())
            {
                getUsers().get(username).isOnline(connectionId);
                if (ConnectionByUser.containsKey(username)){
                    ConnectionByUser.remove(username);
                    ConnectionByUser.putIfAbsent(username,connectionId);
                    System.out.println (username+" Online now: "+Users.get(username).getStatus());
                    System.out.println ("'s ConnectionID is: "+connectionId);
            }

            return true;
        }
        return false;
    }//check unread messages and notifications-to add
    public boolean setOffline (String username){
        System.out.println (username+" is Online right now: "+Users.get(username).getStatus());
        if (Users.get(username).getStatus())//if user exists and online-offline him
        {
            System.out.println("Setting offline");
            ConnectionByUser.remove(username);
            ConnectionByUser.put(username,-1);
            Users.get(username).isOffline();
        return true;
        }
    return false;
    }
    public boolean Follow(String username,String toFollow){
        System.out.println ("username and tofollow is on a list: "+Users.containsKey(username)+","+ Users.containsKey(toFollow));
        if (Users.containsKey(username)&& Users.containsKey(toFollow)&&
                !Users.get(toFollow).getFollowers().contains(username)&&
                !Users.get(username).getFollowing().contains(toFollow))
        {
            getUsers().get(toFollow).getFollowers().add(username);
            getUsers().get(username).getFollowing().add(toFollow);
            return true;
        }
        return false;
    }
    public boolean Unfollow(String username,String toUnfollow){
        if (getUsers().containsKey(username)&& getUsers().containsKey(toUnfollow)&&
            getUsers().get(username).getFollowing().contains(toUnfollow)&&
                getUsers().get(toUnfollow).getFollowers().contains(username))
        {
            getUsers().get(username).getFollowing().remove(toUnfollow);
            getUsers().get(toUnfollow).getFollowers().remove(username);
            return true;
        }
        return false;
    }
    public ConcurrentHashMap<String, User> getUsers() {
        return Users;
    }
    public int getConnectionIdByUsername (String username){
        if (ConnectionByUser.containsKey(username)){return ConnectionByUser.get(username);}
        return -1;
    }
    public LinkedList<String>getUserList(){
        LinkedList<String>list=new LinkedList<>();
        for (String user:Users.keySet()){
            list.add(user);
        }
        return list;
    }
    public LinkedList<String>getUserStats(String username){
        if (Users.containsKey(username)){
            LinkedList<String>list=Users.get(username).getStats();
            return list;
        }
        return null;
    }
    public int SendNotification (String username,NotificationMsg tonotify){
        if (Users.containsKey(username)){
            System.out.println("Next condition(DB)...GO TO USER");
            return Users.get(username).SendNotification(tonotify);
        }
        return -1;
    }
    public LinkedList<String>UserList(){
        System.out.println("Building list(DB)");
        LinkedList<String>list=new LinkedList<String>();
        for (String user:Users.keySet()){
            System.out.println("adding users(DB)");
            list.add(user);
        }
        return list;
    }
    public ConcurrentLinkedQueue<NotificationMsg>getUnreaded(String username){
        ConcurrentLinkedQueue<NotificationMsg>list=new  ConcurrentLinkedQueue<>();
        if (Users.contains(username)){
            list=Users.get(username).getUnreaded();
        }
        return list;
    }
    public void toNotifyLater(String username,NotificationMsg msg){
        System.out.println("Next condition...offliine,notify later");
        System.out.println("Next condition..."+username+" is legal: "+Users.get(username)!=null);
        System.out.println("Next condition...adding Notification to user");
        Users.get(username).addNotification(msg);
    }
    public NotificationMsg checkUnreaded(String username){
        System.out.println("Next condition...moving to user(DB)");
        return Users.get(username).checkUnreaded();
    }
    public boolean PrivateMsg (String username,String sender,PrivateMsg message){
        Users.get(sender).addPm(message.getPM());
        if (Users.containsKey(username)){
            System.out.println (username+" is Legal!");
            boolean result=Users.get(username).PrivateMsg();
            System.out.println ("User is Online: "+result);
            if (!result){
                NotificationMsg toNotify=new NotificationMsg((byte)1,message.getPM(),sender);
                toNotifyLater(username,toNotify);
            }
            return result;
        }
        System.out.println ("BAD CONDITION AT DB");
        return false;
    }
    public boolean DBsearch (String username){
        return Users.containsKey(username);
    }
}
