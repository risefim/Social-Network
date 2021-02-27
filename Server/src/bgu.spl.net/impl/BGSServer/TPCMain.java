package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.api.bidi.OurMessagingProtocol;
import bgu.spl.net.srv.BaseServer;
import bgu.spl.net.srv.BidiMessageEncoderDecoder;
import bgu.spl.net.srv.SharedDB;

import java.util.function.Supplier;

@SuppressWarnings("unchecked")
public class TPCMain {
    public static void main (String[]args){
        SharedDB database=new SharedDB();
        Supplier<OurMessagingProtocol>protocolFactory=()->new OurMessagingProtocol(database);
        Supplier<BidiMessageEncoderDecoder>endecFactory=()->new BidiMessageEncoderDecoder();
        BaseServer server=new BaseServer(Integer.parseInt(args[0]),protocolFactory,endecFactory);
        server.serve();
    }
}
