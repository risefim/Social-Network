package bgu.spl.net.srv;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PostMsg implements Msg {
    private int Opi=5;
    private String sender=null;//set after
    private String post;
    private LinkedList<String>tagged=new LinkedList<>();
    public PostMsg (ConcurrentLinkedQueue<String> list){
        post=list.poll();
        tagged=gettagged();
    }

    public String getSender() {
        return sender;
    }
    public void setsender(String sender) {
        this.sender=sender;
    }
    public String getPost() {
        return post;
    }
    public LinkedList<String> gettagged() {
        String cur="";
        boolean build=false;
        for (int i=0;i<post.length();i++){



            if (post.charAt(i)=='@'&&!build){//start to build tagged
                build=true;
            }
            if (post.charAt(i)==' '&&build){//end of tagged name-reset our parameters
                build=false;
                if (!tagged.contains(cur)&&cur.length()>0){  tagged.add(cur.substring(1));}
                cur="";
            }
            if (build){cur+=post.charAt(i);
            }//seems legit
        }
        if (!cur.equals("")&&!tagged.contains(cur.substring(1))){
                tagged.add(cur.substring(1));//removing '@'



        }
        return tagged;
    }
    public void PrintContent (){
        System.out.println("==Message Content:Post==");
        System.out.println("*OPI: "+Opi);
        System.out.println ("*Post text: "+post);
        System.out.println("*@tagged(not only users): ");
        for (String user:tagged){
            System.out.println ("*tagged: "+user+" name len is: "+user.length());
        }
        System.out.println("==End of content!==");
    }
}
