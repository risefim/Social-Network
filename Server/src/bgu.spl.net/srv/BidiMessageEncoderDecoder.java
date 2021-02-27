package bgu.spl.net.srv;

import bgu.spl.net.api.MessageEncoderDecoder;
import jdk.nashorn.internal.runtime.regexp.joni.constants.OPCode;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

//-----------------------------------------------------------------------------------------------------
public class BidiMessageEncoderDecoder implements MessageEncoderDecoder<Msg> {
    private HashMap<Integer,Integer> OpiZeros =new HashMap<>();
   // private HashMap <Integer,int[]>OpiArgLen=new HashMap<>();
    private int len = 0;
    private int L=0;
    private int curOp=0;
    private int zeros=2;
    private String Collection="";
    private int strLen=0;
    private ConcurrentLinkedQueue<String> list=new ConcurrentLinkedQueue<>();
    private byte[] bytes = new byte[1 << 10]; //start with 1k

    public static void main (String[] args){
        ConcurrentHashMap<Integer,Integer> map=new ConcurrentHashMap<>();
        System.out.println(map.putIfAbsent(1,1));
        System.out.println(map.putIfAbsent(1,1));
        BidiMessageEncoderDecoder bidi=new BidiMessageEncoderDecoder();
        // byte[]arr5={0,1,77,111,114,116,121,0,97,49,50,51,0};//ex of reg input
        //byte[]arr5={0,2,77,111,114,116,121,0,97,49,50,51,0};//ex of login input
        //byte[]arr5={0,8,77,111,114,116,121,0};//ex of Stat input
        //byte[]arr5={0,3};//ex of logout input
        //byte[]arr5={0,2,77,111,114,116,121,0,97,49,50,51,0};//ex of login input
        //00 04 00 00 02 41 44 49 00 4D 61 74 00
        //byte[]arr5={0,4,0,0,2,65,68,73,0,77,97,116,0};//ex of followunfoolw input
        //byte[]arr5={0,5,64,78,111,98,111,100,121,32,64,101,120,105,115,116,115,32,77,97,116,0};//ex of post input
        byte[]arr5={0,6,77,97,116,0,78,111,98,111,100,121,32,101,120,105,115,116,115,0};
//
//        byte[] arr = bidi.shortToBytes((short)'~');
//        byte[]arr2={77,111,114,116,121,0};
//        byte[]arr5={0,4,49,48,50,0,77,111,114,116,121,0,77,111,114,116,121,0};//follow-unfollow ex


        Msg msg=null;
        for (int i=0;i<arr5.length;i++){
            msg= bidi.decodeNextByte(arr5[i]);
            if (msg!=null){
                {
                    System.out.println ("Is a Reg Msg: "+(msg instanceof RegMsg));
                    ((PrivateMsg)msg).PrintContent();
                }
            }
        }
        if (msg==null) {System.out.println ("Bad msg structure");}
    }

    public BidiMessageEncoderDecoder(){
        this.OpiZeros.put (1,2);
        this.OpiZeros.put (2,2);
        this.OpiZeros.put (3,0);
        this.OpiZeros.put (4,1);
        this.OpiZeros.put (5,1);
        this.OpiZeros.put (6,2);
        this.OpiZeros.put (7,0);
        this.OpiZeros.put (8,1);
    }
    @Override
    public Msg decodeNextByte(byte nextByte) {
        System.out.println("nextByte is: "+nextByte);
        System.out.println("========================");
        System.out.println ("Current collection is: "+Collection);
        if (L==0){
            Collection="";

        }
        Collection+=nextByte+",";
        boolean flag=!(L==0&nextByte==10);
        if (L<2){

            bytes[L]=nextByte;// next byte
        }
        //--------OP check----------------
        if (L==1){
            System.out.println ("Problem bytes are: "+bytes[0]+","+bytes[1]);
            System.out.println ("Stage 1 Completed-Op byte");
            byte[]opCode={bytes[0],bytes[1]};
            this.curOp=(int)bytesToShort(opCode);//first two bytes are OpCode-let's check type of msg
            this.zeros=OpiZeros.get(curOp);//for register message:define relevant array or hash
            this.strLen=0;
            System.out.println ("Stage 2 Completed-Op type; OP type is:"+curOp);
            if (curOp==3||curOp==7){//Empty msgs
                System.out.println ("Empty msg of type: "+curOp);
                L=0;//end of msg
                if (curOp==3){
                    return new LogoutMsg();
                }
                else{
                    return new UserListMsg();
                }
            }
        }
        //-----Decoding------------
        if (L>1){
            if (curOp==1||curOp==2||curOp==5||curOp==6||curOp==8){//Op's with only zero bounds
                if (nextByte=='\0'){//we reached end of string but not end of msg
                    String name= popString();
                    System.out.println("adding to list: " +name);
                    list.add(name);
                    zeros--;
                    if (zeros == 0) {//end of this Op's msgs
                        L = 0;//start of new msg
                        if (curOp==1){
                            RegMsg output=new RegMsg(list);
                            System.out.println ("Register msg is done!");
                            return output;

                        }
                        if (curOp==2){
                            LoginMsg output=new LoginMsg(list);
                            System.out.println ("Login msg is done!");
                            return output;
                        }
                        if (curOp==5){
                            PostMsg output=new PostMsg(list);
                            System.out.println ("Post msg is done!");
                            output.PrintContent();
                            return output;
                        }
                        if (curOp==6){
                            PrivateMsg output=new PrivateMsg(list);
                            System.out.println ("Private msg is done!");
                            return output;
                        }
                        if (curOp==8){
                            StatMsg output=new  StatMsg(list);
                            System.out.println ("Stat msg is done!");
                            return output;
                        }
                        RegMsg output=new RegMsg(list);
                        System.out.println ("Register msg is done!");
                        return output;
                    }
                }
                else {
                    pushByte(nextByte);
                }
            }
           if (curOp==4){//(un)follow:complex msg
                       if (L==2){//L=2->follow unfollow sign
                           System.out.println ("Stage I:Follow or Unfollow???");
                           if (nextByte==0){
                               list.add("0");
                           }
                           if (nextByte==1){
                               list.add("1");
                           }
                       }
                       if (L>2&&L<5){
                           System.out.println ("Stage II:Setting bytes of users num...");
                           pushByte(nextByte);
                       }
                   if (L==5){//2 bytes of num of users
                       System.out.println ("Stage III:Setting bytes of users num...");
                       System.out.println ("**current len is: "+len);
                       byte[]numofusers={bytes[len-2],bytes[len-1]};
                       short num=bytesToShort(numofusers);
                       zeros=(int)num;//num of users represented in bytes
                       System.out.println("Num of Zeros to sign is: "+zeros);
                       list.add(""+num);
                       popString();
                            pushByte(nextByte);
                           }
                   if (L>5){
                       if (nextByte!='\0'){//relevant byte
                           pushByte(nextByte);
                       }
                       else {
                           if (zeros==1){//end of message
                               L=0;
                               System.out.println("Finished our zeros!");
                               if (len!=0){
                                   list.add(popString());
                               }
                               System.out.println("Empty List,why?");
                               FollowUnMsg output=new FollowUnMsg(list);
                               return output;
                           }
                           else{
                               list.add(popString());
                               zeros--;
                           }
                       }
                   }
               }
          }

        L++;
        return null;
    }

    @Override
    public byte[] encode(Msg message) {
        if (message instanceof Msg){
            System.out.println ("Encoding...");

            if (message instanceof AckMsg){//encode to ACK msg
                //defining type of ACK msg by ReturnOpi
                int BackOpi=((AckMsg)message).getBackOpi();
                byte[]OpCode=shortToBytes((short)10);
                byte[]AckOp=shortToBytes((short)BackOpi);
                if (BackOpi!=4&&BackOpi!=7&&BackOpi!=8){//simple ACK's'
                    byte[]output={OpCode[0],OpCode[1],AckOp[0],AckOp[1]};
                    System.out.println("ACK Response is done!");
                    System.out.println ("ACK contains next bytes: |0:"+output[0]+"|1: "+output[1]+"|2: "+output[2]+ "|3: "+output[3]);
                    return output;
                }
                else{
                    Collection="";
                    if (BackOpi==4||BackOpi==7){//Follow or userlist msg:same structure
                    byte[]numOfUsers=shortToBytes((short)((AckMsg)message).getOutputList().size());
                    byte[]output=new byte[1<<16];//allocate standard array with next size
                    output[0]=OpCode[0];
                    Collection+=output[0]+",";
                    output[1]=OpCode[1];//
                    Collection+=output[1]+",";
                    output[2]=AckOp[0];
                    Collection+=output[2]+",";
                    output[3]=AckOp[1];//
                    Collection+=output[3]+",";
                    output[4]=numOfUsers[0];
                    Collection+=output[4]+",";
                    output[5]=numOfUsers[1];//
                    Collection+=output[5]+",";
                    int i=6;
                    for (String user:((AckMsg)message).getOutputList()){
                        byte[]userbytes=user.getBytes();
                        if (output.length-i-1<userbytes.length){
                            output = Arrays.copyOf(output, output.length* 2);//extend if necessary
                        }
                        for (int j=0;j<userbytes.length;j++){//chaining bytes of username
                            output[i]=userbytes[j];
                            Collection+=output[i]+",";
                            i++;
                        }
                        output[i]='\0';//chaining terminating zero
                        i++;
                        Collection+=output[i]+",";
                    }
                        if ((i+1)!=output.length){
                            output = Arrays.copyOf(output, i+1);//fit size
                        }
                        System.out.println("ACK Response is done!");
                        System.out.println("Current Collection of bytes is: "+Collection);
                        Collection="";
                        return output;
                    }
                    if (BackOpi==8){//Stats Ack msg
                        byte[]output=new byte[10];
                        output[0]=OpCode[0];
                        Collection+=output[0];
                        output[1]=OpCode[1];//
                        Collection+=output[1];
                        output[2]=AckOp[0];
                        Collection+=output[2];
                        output[3]=AckOp[1];//
                        Collection+=output[3];
                        byte[] postsnum=shortToBytes((short)Integer.parseInt(((AckMsg)message).getOutputList().poll()));
                        output[4]=postsnum[0];
                        Collection+=output[4];
                        output[5]=postsnum[1];//
                        Collection+=output[5];
                        byte[] followers=shortToBytes((short)Integer.parseInt(((AckMsg)message).getOutputList().poll()));
                        output[6]=followers[0];
                        Collection+=output[6];
                        output[7]=followers[1];//
                        Collection+=output[7];
                        byte[] following=shortToBytes((short)Integer.parseInt(((AckMsg)message).getOutputList().poll()));
                        output[8]=following[0];
                        Collection+=output[8];
                        output[9]=following[1];//
                        Collection+=output[9];
                        System.out.println("Current Collection of bytes is: "+Collection);
                        Collection="";
                        return output;
                    }
                }
            }//Preparing Ack msg


            if (message instanceof ErrorMsg){
                byte[]OpCode=shortToBytes((short)11);
                int BadOpi=((ErrorMsg)message).getBadOpi();
                byte[]BadOp=shortToBytes((short)BadOpi);
                byte[]output={OpCode[0],OpCode[1],BadOp[0],BadOp[1]};
                System.out.println("Error Response is done!");
                System.out.println ("Error contains next bytes: 0: "+output[0]+" 1: "+output[1]+" 2: "+output[2]+ " 3: "+output[3]);
                return output;
            }//Preparing Err msg


            if (message instanceof NotificationMsg){
                Collection="";
                System.out.println ("Preparing Notification");
                byte[]OpCode=shortToBytes((short)9);//OpiCode of Notification
                byte sign='\1';
                if (((NotificationMsg) message).getPostType()==0)
                    sign='\0';
                byte[]sender=((NotificationMsg) message).getSender().getBytes();
                byte[]text=((NotificationMsg) message).getPostText().getBytes();
                byte[]output=new byte[5+sender.length+text.length];//allocate fit array
                int put=3;
                output[0]= OpCode[0];
                Collection+=output[0]+",";
                output[1]= OpCode[1];
                Collection+=output[1]+",";
                output[2]=sign;
                Collection+=output[2]+",";
                    for (int i=0;i<sender.length;i++){
                        output[put]=sender[i];
                        Collection+=output[put]+",";
                        put++;
                    }
                output[put]='\0';
                Collection+=output[put]+",";
                put++;
                    for (int j=0;j<text.length;j++){
                        output[put]=text[j];
                        Collection+=output[put]+",";
                        put++;
                    }
                output[put]='\0';
                Collection+=output[put]+",";
                System.out.println("Notif Collection to send is: "+Collection);
                Collection="";
                return output;
            }//Preparing Notif msg
        }
        System.out.println ("If We reached this line,We're in trouble!(MED)");

        return null;
    }

    private String popString() {
        String result = new String(bytes, 0, len, StandardCharsets.UTF_8);
        System.out.println("Current string to pop is: "+result+" with length of: "+len);
        len = 0;

        return result;
    }

    private void pushByte(byte nextByte) {
        if (len >= bytes.length) {
            bytes = Arrays.copyOf(bytes, len * 2);
        }

        bytes[len++] = nextByte;
    }

    public short bytesToShort(byte[] byteArr)
    {
        short result = (short)((byteArr[0] & 0xff) << 8);
        result += (short)(byteArr[1] & 0xff);
        return result;
    }
    public byte[] shortToBytes(short num)
    {
        byte[] bytesArr = new byte[2];
        bytesArr[0] = (byte)((num >> 8) & 0xFF);
        bytesArr[1] = (byte)(num & 0xFF);
        return bytesArr;
    }





}