package files;

import java.io.IOException;
import java.io.RandomAccessFile;

public class RandomAccess {
    /**
     * Treat the file as an array of (unsigned) 8-bit values and sort them
     * in-place using a bubble-sort algorithm.
     * You may not read the whole file into memory!
     *
     * @param file
     */
    public static void sortBytes(RandomAccessFile file) throws IOException {
        // TODO: implement
        int numLineInFile = (int)file.length();
        for (int i = 0; i < numLineInFile - 1; i++){
            for (int j = 0; j < numLineInFile - i - 1; j++){
                file.seek(j);
                int currentByte = (int)file.read();
                file.seek(j+1);
                int nextBytes = (int)file.read();
                if(currentByte>nextBytes){
                    file.seek(j);
                    file.write(nextBytes);
                    file.seek(j+1);
                    file.write(currentByte);
                }
            }

        }

    }

    /**
     * Treat the file as an array of unsigned 24-bit values (stored MSB first) and sort
     * them in-place using a bubble-sort algorithm.
     * You may not read the whole file into memory!
     *
     * @param file
     * @throws IOException
     */
    public static void sortTriBytes(RandomAccessFile file) throws IOException {
        // TODO: implement
        int numLineInFile = (int)file.length()/3;
        for (int i = 0; i < numLineInFile - 1; i++){
            for (int j = 0; j < numLineInFile - i - 1; j++){
                file.seek(j*3);
                int group1 = (file.read() << 16) | (file.read() << 8) | file.read();
                file.seek((j+1)*3);
                int group2 = (file.read() << 16) | (file.read() << 8) | file.read();
                if(group1>group2){
                    file.seek(j*3);
                    file.write((group2 >> 16) & 0xFF); // MSB
                    file.write((group2 >> 8) & 0xFF);  // Middle byte
                    file.write(group2 & 0xFF);         // LSB
                    file.seek((j+1)*3);
                    file.write((group1 >> 16) & 0xFF); // MSB
                    file.write((group1 >> 8) & 0xFF);  // Middle byte
                    file.write(group1 & 0xFF);         // LSB
                }
            }

        }


    }
}
