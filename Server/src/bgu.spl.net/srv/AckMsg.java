package bgu.spl.net.srv;

import java.util.LinkedList;

public class AckMsg implements Msg {
    private int Opi=10;
    private int BackOpi;
    private LinkedList<String>outputList=new LinkedList<>();

    public AckMsg (int BackOpi,LinkedList<String> output){
        this.BackOpi=BackOpi;
        this.outputList=output;//if list eq null->simple ack,else=complex
    }

    public int getBackOpi() {
        return BackOpi;
    }

    public LinkedList<String> getOutputList() {
        return outputList;
    }

    public int getOpi() {
        return Opi;
    }
    public void PrintContent (){
        System.out.println("==Message Content:ACK==");
        System.out.println("*OPI: "+Opi);
        System.out.println("*BackOPI: "+BackOpi);
        System.out.println("==End of content!==");
    }
}
