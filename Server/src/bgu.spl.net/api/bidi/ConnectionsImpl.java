package bgu.spl.net.api.bidi;
import bgu.spl.net.srv.AckMsg;
import bgu.spl.net.srv.BlockingConnectionHandler;
import bgu.spl.net.srv.ConnectionHandler;

import java.util.HashMap;
import java.util.Iterator;




public class ConnectionsImpl<T> implements Connections<T>{
    private HashMap <Integer, ConnectionHandler<T>> ActiveMap=new HashMap<>();//
    private HashMap <Integer,String>UserByConnId=new HashMap<>();//necessary

    public ConnectionsImpl (){}

    @Override
    public boolean send(int connectionId, T msg) {
        if (ActiveMap.containsKey(connectionId)) {
            ConnectionHandler<T> handler=ActiveMap.get(connectionId);
            handler.send(msg);
            return true;
        }
        return false;
    }

    @Override
    public void broadcast(T msg) {
        Iterator <Integer>it=ActiveMap.keySet().iterator();
        while (it.hasNext()){
            Integer cur=it.next();
            ActiveMap.get(cur).send(msg);
        }

    }
    @SuppressWarnings("unchecked")
    public void ConnectAndId (ConnectionHandler handler,int connectionId){
        ActiveMap.putIfAbsent (connectionId,handler);

    }
    public void disconnect(int connectionId){//closes socket
        if (ActiveMap.containsKey(connectionId)){
            ActiveMap.remove(connectionId);
            UserByConnId.remove(connectionId);
        }
    }


}