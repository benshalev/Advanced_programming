package files;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class Streams {
    /**
     * Read from an InputStream until a quote character (") is found, then read
     * until another quote character is found and return the bytes in between the two quotes.
     * If no quote character was found return null, if only one, return the bytes from the quote to the end of the stream.
     *
     * @param in
     * @return A list containing the bytes between the first occurrence of a quote character and the second.
     */
    public static List<Byte> getQuoted(InputStream in) throws IOException {
        // TODO: Implement
        List<Byte> quotedBytes = new ArrayList<>();
        int data;
        int countQuote=0;
        try{
            while((data = in.read()) != -1 && countQuote<2){
                if ((char)data == '"' ){
                    countQuote++;
                    continue;
                }
                if(countQuote==1){
                    quotedBytes.add((byte) data);
                }
            }
            if (countQuote==0){
                return null;
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return quotedBytes;
    }


    /**
     * Read from the input until a specific string is read, return the string read up to (not including) the endMark.
     *
     * @param in      the Reader to read from
     * @param endMark the string indicating to stop reading.
     * @return The string read up to (not including) the endMark (if the endMark is not found, return up to the end of the stream).
     */
    public static String readUntil(Reader in, String endMark) throws IOException {
        // TODO: Implement
    StringBuilder sb = new StringBuilder();
    int data;
    int lengthOfEndMarks = endMark.length();
    try{
        while((data = in.read()) != -1 ){
            char c = (char)data;
            sb.append(c);
            if(sb.length() >= (lengthOfEndMarks)){
                String lastChars = sb.substring(sb.length() - endMark.length());
                if (lastChars.equals(endMark)) {
                    return sb.substring(0, sb.length() - endMark.length());
                }
            }
        }
    }
    catch (IOException e){
        e.printStackTrace();
    }
        return sb.toString();
    }

    /**
     * Copy bytes from input to output, ignoring all occurrences of badByte.
     *
     * @param in
     * @param out
     * @param badByte
     */
    public static void filterOut(InputStream in, OutputStream out, byte badByte) throws IOException {
        // TODO: Implement
        int data;
        try{
            while((data = in.read()) != -1 ){
                if((byte)data!=badByte){
                    out.write(data);
                }
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Read a 40-bit (unsigned) integer from the stream and return it. The number is represented as five bytes,
     * with the most-significant byte first.
     * If the stream ends before 5 bytes are read, return -1.
     *
     * @param in
     * @return the number read from the stream
     */
    public static long readNumber(InputStream in) throws IOException {
        // TODO: Implement
        int bytesNeeded = 5;
        long result = 0;
        for (int i = 0; i < bytesNeeded; i++) {
            int data = in.read();
            if (data == -1) {
                return -1;
            }
            result = result << 8  | (data & 0xFF);
        }
        return result;
    }
}