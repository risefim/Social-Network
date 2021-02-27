package bgu.spl.net.srv;

public class ErrorMsg implements Msg {
    private int Opi=11;
    private int BadOpi;

    public ErrorMsg (int BadOpi){
        this.BadOpi=BadOpi;
    }

    public int getBadOpi() {
        return BadOpi;
    }

    public int getOpi() {
        return Opi;
    }
    public void PrintContent (){
        System.out.println("==Message Content:Error==");
        System.out.println("*OPI: "+Opi);
        System.out.println("==End of content!==");
    }
}
