package bgu.spl.net.srv;

public class NotificationMsg implements Msg {

    private int Opi=9;
    private byte PostType;
    private String PostText;
    private String Sender;

    public NotificationMsg (byte type,String text,String sender){
        PostType=type;
        PostText=text;
        Sender=sender;

    }

    public int getPostType() {
        return PostType;
    }

    public String getPostText() {
        return PostText;
    }


    public String getSender() {
        return Sender;
    }
    public void PrintContent (){
        System.out.println("==Message Content:Notification==");
        System.out.println("*OPI: "+Opi);
        System.out.println("*Follow/UnFollow: "+PostType);
        System.out.println("*Text: "+PostText);
        System.out.println ("*Sender: "+Sender);
        System.out.println("==End of content!==");
    }
}
