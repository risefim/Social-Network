package bgu.spl.net.srv;

public class LogoutMsg implements Msg {
    private int Opi = 3;

    public LogoutMsg() {
    }

    public int getOpi() {
        return Opi;
    }

    public void PrintContent() {
        System.out.println("==Message Content:LogOut==");
        System.out.println("*OPI: " + Opi);
        System.out.println("==End of content!==");
    }
}
