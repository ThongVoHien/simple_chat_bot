import java.io.*;
import java.nio.charset.*;
import java.net.*;
import java.util.stream.*;

/** send a message to a backend */
public class Sender {
    static final int maxline = 100;
    
    /** Main function for the sender
     @param args input from the terminal */
    public static void main(String[] args) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Input the server ip for connection:");
            String host = br.readLine();
            
            System.out.println("Input the port number for connection:");
            String tmp = br.readLine();
            
            int port = Integer.parseInt(tmp);
            
            System.out.println("Initializing for network communication... ");
            Socket outSock = new Socket(host, port);
            
            InputStream inStream = outSock.getInputStream();
            OutputStream outStream = outSock.getOutputStream();
            /* assert:  socket and stream initialized */
            
            Message ack;
            
            byte[] outBuff = new byte[maxline];
            int count;  // to hold number of bytes read
            String content, type;
            System.out.println("Enter message to start:");
            for (;;){
                count = System.in.read(outBuff);
                content = new String(outBuff,0,count-1);
                /* assert:  input line stored in outBuff[0..count-1] */
                
                Message m = new Message("getFile",content);
                m.send(outStream);
                
                ack = new Message(inStream);
                if ( ack.type.equals("ACK") ){
                    System.out.println("Robot: " + ack.content);
                    if (content.equals("END")) break;
                }
                else System.out.println("Fail to send question: " + ack.content);

                /* successful write on socket */
            }
            
            outSock.close();
        }
        catch (IOException e) {
            System.err.println("Sender failed.");
            System.err.println(e.getMessage());
            System.exit(1);  // an error exit status
            return;
        }
    }
}
