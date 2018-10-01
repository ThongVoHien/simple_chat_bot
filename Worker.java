import java.io.*;
import java.net.*;
import java.lang.*;

/** create a class for receiving message from a sender */
public class Worker implements Runnable{
    String DatabaseFile = "Database.txt";
    String questionFile = "newQuestion.txt";
    
    Socket sock;

    /** Constructor for Worker
	@param s a socket */
    Worker(Socket s) { sock = s; }

    /** method for Worker */
    public void run(){
        System.out.println("Successfully create a socket");
        try{
            OutputStream os = sock.getOutputStream();
            Message ack = new Message("ACK"," ");

            Message m;
            for (;;){
                m = new Message(sock.getInputStream());
                System.out.println(m.type + ',' + m.content);

                getAnswer(m);
                
                if (m.content.equals("END")) break;
            }
            sock.close();
        }catch(IOException e){
            System.out.println("Exception on IO: " + e.getMessage());
        }
    }
    
    /** method for reading file */
    private void getAnswer(Message m){
        System.out.println("Start get answer ");
        Message reply;
        File file;
        FileInputStream fstream = null;
        try{
            file = new File(DatabaseFile);
            fstream = new FileInputStream(file);
            if (!file.exists()){
                System.out.println("Database file does not exist");
                reply = new Message("NACK", "There is an error");
                reply.send(sock.getOutputStream());
                return ;
            }
            if (!file.isFile()){
                System.out.println("Database file is not a file");
                reply = new Message("NACK", "There is an error");
                reply.send(sock.getOutputStream());
                return ;
            }
            if (!file.canRead()){
                System.out.println("Database file is unreadable");
                reply = new Message("NACK", "There is an error");
                reply.send(sock.getOutputStream());
                return ;
            }
            if (file.isDirectory()){
                file = new File(file, file.getName());
            }
            byte[] buffer = new byte [8192];
            int bytes_read = fstream.read(buffer);
            String tmp = new String(buffer,0,bytes_read-1);
            String answer = "I don't understand the question";
            String [] lines = tmp.split("\n");
            for (int i = 0; i < lines.length; i+=2)
                if ( lines[i].equals(m.content) )
                    answer = lines[i+1];
            if ( answer.equals("I don't understand the question") ){
                System.out.println("The database does not have this question");
                addNewQuestion( m.content);
                System.out.println("Finish adding the question to the database");
            }
            reply = new Message("ACK", answer);
            reply.send(sock.getOutputStream());
            
            System.out.println("Successfully get answer " + answer);
        }
        catch (FileNotFoundException e){
            try{
                reply = new Message("NACK", "File does not exist");
                reply.send(sock.getOutputStream());
                return ;
            }
            catch (IOException i){
                System.err.println("Exception caught: " + i.getMessage());
                return ;
            }
        }
        catch (IOException e){
            System.err.println("Exception caught: " + e.getMessage());
            return ;
        }
        finally {
            if (fstream != null)
                try { fstream.close(); } catch (IOException e) { ; }
        }
    }
    /** method for modifying file */
    private void addNewQuestion(String s){
        System.out.println("Start adding new question ");
        File file;
        FileOutputStream fstream = null;
        try{
            s = s + '\n';
            file = new File(questionFile);
            fstream = new FileOutputStream(file, true);
            if (!file.exists()){
                System.out.println("newQuestion file does not exist");
                return ;
            }
            if (!file.isFile()){
                System.out.println("newQuestion file is not a file");
                return ;
            }
            if (!file.canRead()){
                System.out.println("newQuestion file is unreadable");
                return ;
            }
            if (file.isDirectory()){
                file = new File(file, file.getName());
            }
            byte[] buffer = s.getBytes();
            fstream.write(buffer);
            
            System.out.println("Successfully add question " + s);
        }
        catch (FileNotFoundException e){
            System.err.println("newQuestion file does not exist");
            return ;
        }
        catch (IOException e){
            System.err.println("Exception caught: " + e.getMessage());
            return ;
        }
        finally {
            if (fstream != null)
                try { fstream.close(); } catch (IOException e) { ; }
        }
    }
}
	



