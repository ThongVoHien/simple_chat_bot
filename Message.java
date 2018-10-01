/** A class for transferring Message.
 @author V. Thong, 1/00 */

import java.io.*;

/** class for dealing with message */
public class Message{

    // class variables

    /** a single character that indicates the end of each part of a Message when it is expressed as an array of byte */
    protected final String TERMINATOR = "~";

    /** the buffer size for input */
    protected final int MAXBUFF = 8192;
    
    // state variables
    
    /** the type of the message */
    String type;

    /** content of the message */
    String content;
    
    // constructors

    /** Initializes the two state variables to those String values
	@param str1 The type of the message
	@param str2 The content of the message */
    Message(String str1, String str2){
        type = str1;
        content = str2;
    }

    /** Attempts to read a Message represented as an array of byte initialize the two state variables from that array, using the delimiter TERMINATOR to determine where the two values end
     @param is InputStream which is associated with a socket
     @exception EOFException Thrown when encounter end of stream
     @exception IOException Thrown when encounter other */
    Message(InputStream is) throws EOFException, IOException{
        byte[] inBuff = new byte[MAXBUFF];
        int count = 0;  // to hold number of bytes read
        try{
            count = is.read(inBuff);
        }
        catch (IOException e){
            System.out.println(e.getMessage());
            System.exit(0);
        }
        String s;
        s = new String(inBuff,0,count);
        String[] str = s.split(TERMINATOR);
        type = str[0];
        content = str[1];
    }

    // methods

    /** Produce a byte-array representation of this Message
     @return an array of byte representing a Message
     @exception UnsupportedEncodingException
     Thrown if there is an error in encoding */
    public byte[] getBytes() throws UnsupportedEncodingException{
        String str = type + TERMINATOR + content + TERMINATOR;
        return str.getBytes();
    }

    /** Produce a readable representation of this Message
     @return a String representing a printable version of the Message */
    public String toString(){
        return "[type = " + type + ", content = " + content + "]";
    }

    /** Transmit this message on a stream, such as a socket output stream
     @param str Stream on which to transmit the message, in UTF-8 encoding
     @exception IOException
     Throw if there is a problem with <code>str.write()</code>*/
    public void send(OutputStream str) throws IOException{
        byte[] b = (type + TERMINATOR + content + TERMINATOR).getBytes();
        str.write(b);
    }

    /** Test for this class.
     @param args input from terminal */
    public static void main(String[] args){
        
    }
}



