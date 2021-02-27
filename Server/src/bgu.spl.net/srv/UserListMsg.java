package bgu.spl.net.srv;

public class UserListMsg implements Msg{

    private int Opi=7;
    private String sender=null;


    public int getOpi() {
        return Opi;
    }
    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
    public void PrintContent (){
        System.out.println("==Message Content:Userlist==");
        System.out.println("*OPI: "+Opi);
        System.out.println("==End of content!==");
    }
}