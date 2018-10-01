import java.io.*;
import java.nio.charset.*;
import java.net.*;
import java.util.stream.*;

/** send a message to a backend */
public class Server {
    String questionFile = "newQuestion.txt";
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
            Server s = new Server();
            
            for (;;){
                System.out.println("Start sending file...");
                s.checkFile(outSock);
                Thread.sleep(7000);
            }
            
        }
        catch (IOException e) {
            System.err.println("Failed: ");
            System.err.println(e.getMessage());
            System.exit(1);  // an error exit status
            return;
        }
        catch (InterruptedException e) {
            System.err.println("Failed: ");
            System.err.println(e.getMessage());
            System.exit(1);  // an error exit status
            return;
        }
    }
    
    /** method for dealing with get file */
    public void checkFile(Socket outSock){
        Message reply;
        File file;
        FileInputStream fstream = null;
        try{
            System.out.println("Start reading file...");
            file = new File(questionFile);
            fstream = new FileInputStream(file);
            if (!file.exists()){
                reply = new Message("NACK", "File does not exist: " + file );
                reply.send(outSock.getOutputStream());
                return ;
            }
            if (!file.isFile()){
                reply = new Message("NACK", "It is not a file: " + file );
                reply.send(outSock.getOutputStream());
                return ;
            }
            if (!file.canRead()){
                reply = new Message("NACK", "File is unreadable: " + file );
                reply.send(outSock.getOutputStream());
                return ;
            }
            if (file.isDirectory()){
                file = new File(file, file.getName());
            }
            byte[] buffer = new byte [8192];
            int bytes_read = fstream.read(buffer);
            String tmp = new String(buffer,0,bytes_read-1);
            reply = new Message("ACK", tmp);
            System.out.println("Done reading file...");
            reply.send(outSock.getOutputStream());
            System.out.println("Done sending file:" + tmp);
        }
        catch (FileNotFoundException e){
            System.err.println("Failed: ");
            System.err.println(e.getMessage());
            try{
                reply = new Message("NACK", "File does not exist");
                reply.send(outSock.getOutputStream());
                return ;
            }
            catch (IOException i){
                System.err.println("Exception caught: " + i.getMessage());
                return ;
            }
        }
        catch (IOException e){
            System.err.println("Failed: ");
            System.err.println(e.getMessage());
            System.err.println("Exception caught: " + e.getMessage());
            return ;
        }
        finally {
            if (fstream != null) try { fstream.close(); } catch (IOException e) { ; }
        }
    }
}
