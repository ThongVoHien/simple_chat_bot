import java.io.*;
import java.net.*;

/** create a backend to receive message*/
public class Backend {
    static final int maxinBuff = 1000;
    
    /** main function for the backend
     @param args the port number of network */
    public static void main(String[] args) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Input the port number for connection:");
            String tmp = br.readLine();
            int port = Integer.parseInt(tmp);
            System.out.println("Initializing for network communication... ");
            ServerSocket servSock = new ServerSocket(port);
            /* assert:  ServerSocket successfully created */
            
            while (true) {
                System.out.println("Waiting for an incoming connection... ");
                Socket inSock = servSock.accept();
                Thread t = new Thread(new Worker(inSock));
                t.start();
            }
        }
        catch (IOException e) {
            System.err.println("Receiver failed.");
            System.err.println(e.getMessage());
            System.exit(1);  // an error exit status
            return;
        }
        
    }
}
