package bgu.spl.net.api.bidi;

//import bgu.spl.net.api.bidi.Messages.User;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ResourceHolder {// singelton
//
//    private ConcurrentHashMap<String,String> isConnected;
//    private  List<User> users ;
//
//
//    private static class ResourceHolderHolder{
//        private static ResourceHolder resouce_holder = new ResourceHolder();
//    }
//
//    public static ResourceHolder getInstance() {
//        return ResourceHolderHolder.resouce_holder;
//    }
//
//
//    public ResourceHolder(){
//        isConnected=new ConcurrentHashMap<>();
//        users = new LinkedList<>();
//    }
//    public Boolean IsConnected(String key)
//    {
//        return key!=null && this.isConnected.containsKey(key);
//    }
//    public void ConnectUser(String key, String connectionId)
//    {
//        this.isConnected.put(key, connectionId);
//    }
//    // getBasicUser - returns the matching BASIC user
//
//    public boolean passwordMatch(String key, String password)
//    {
//        return getBasicUser(key).getPassword().equals(password);
//    }
//
//    public void addUser(User user){
//        users.add(user);
//    }
//
//    public User getBasicUser(String key)
//    {
//        for(User basicUser : users){
//            if(basicUser.getUsername().equals(key))
//                return basicUser;
//        }
//        return null;
//    }
//    public void disconnectUser(String key)
//    {
//        this.isConnected.remove(key);
//    }
//
//    public ConcurrentHashMap<String,String>  getConnectedUsers(){
//        return isConnected;
//
//    }
//
//
//
//
//
//
//
//

}
