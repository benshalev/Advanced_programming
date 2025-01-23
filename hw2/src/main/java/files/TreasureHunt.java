//the name of the package will be files, we chose this name but this file package have to be in dir name files
package files;

//import the IOException class from from the package java.io
//Which is used to handle problems related to input/output, such as reading or writing files.
//Handle it in the method itself with try-catch.
//Pass it on with throws IOException like in this code.
import java.io.IOException;

// This is an import line that brings in the FileChannel class from the java.nio.channels package.
// Unlike basic approaches like FileInputStream, it allows:
//  Direct reading and writing to specific locations in a file.
//  Efficient management of large files.
//  Use of "memory-mapped files" capabilities.
import java.nio.channels.FileChannel;

// It imports the ByteBuffer class from the java.nio package.
// with useage in this class we can allocate byte to work with with number of byte
// Two main modes:
//  1.Write: Mode where you fill data into the buffer.
//  2.Read: Mode where you read data from the buffer.
//  ! To switch from write mode to read mode, you call buffer.flip().
import java.nio.ByteBuffer;

public class TreasureHunt {
    /**
     * Find the treasure by following the map.
     * <p>
     * Starting with the firstClue, and until it has found the treasure (the decoder returned -1), this method should
     * repeat the following actions:
     * <ol>
     *     <li>Decode the clue (using the decoder) to get the index of the next clue. </li>
     *     <li>Read the next clue from the map. Each clue consists of the 48 bits of the map starting at the index
     *     returned from the decoder (treat anything beyond the end of the map as 0).
     * <p>
     *     The index of a clue is given in bits from the beginning of the map,
     *     where 0 is the MSB of the first byte (and map_size_in_bytes*8-1 is the LSB of the last byte). </li>
     * </ol>
     *
     * @param map       This is a {@link FileChannel} containing the encoded treasure map.
     * @param decoder   The decoder used to find the location of the next clue
     * @param firstClue The first clue.
     * @return The index of the treasure in the file (in bits)
     * @throws IOException
     */

    //  The readClue method:
    // Reads a clue from the map (via FileChannel).
    // Uses the bitIndex to know where to read from.
    // Returns the clue in the format of a long number, which contains exactly 48 bits.
    private static long readClue(FileChannel map, long bitIndex) throws IOException {
            // buffer is the variable that contains the reference to the ByteBuffer object.
            // The buffer will be used to temporarily store the data read from the file.
            // ByteBuffer.allocate(8): Creates a new buffer of size 8 bytes (64 bits).
            // We know that each hint is 48 bits, so an 8-byte buffer provides space to safely read the data even if it is in the middle of a byte.
        ByteBuffer buffer = ByteBuffer.allocate(8);
            //a new long variable that will contain the index of bytes that FileChannel use to read
        long byteIndex = bitIndex / 8;
            //We calculate the remainder of the conversion we performed to bits to know whether 
            //the index for reading starts in the middle of a particular bit.
        int bitOffset = (int) (bitIndex % 8);
            // A method that clears the internal state of the ByteBuffer and prepares it for filling with new data (writing).
        buffer.clear();
            // reads data from the map and store in bytesRead:
            //the map reference to the FileChannel that use the buffer to write in it and the index for the
            // start position to read the is executed based on the exact location of the current hint (byteIndex),
            // and the data is loaded into the ByteBuffer so that it can be processed later.
        int bytesRead = map.read(buffer, byteIndex);
            //A ByteBuffer method that changes the buffer state from write to read.
        buffer.flip();
            // The clue is where the final clue that will be read from the file will be stored, with 64 bits
        long clue = 0;
            //In the for loop, we build the clue by reading up to 8 bytes from the buffer that contained the data read from the file.
            //At each iteration, the current byte from the buffer (if available) is added to the clue, bitwise shifting it left by 8 bits to make room for the new value.
            //The loop adjusts itself to the number of bytes actually read from the file (bytesRead), so if fewer than 8 bytes were read,
            //the remaining iterations add no information, and the clue is filled with 0 for the missing spaces.
        for (int i = 0; i < 8; i++) {
            clue <<= 8;
            if (i < bytesRead) {
                clue |= (buffer.get() & 0xFF);
            }
        }
            // Adjust for bit offset because we allocate 8 bytes
        // 64 - (bitOffset + 48)
        //For example: if the hint starts at an offset of 2 bits,
        //we will move right by 14 bits to "ignore" the unnecessary bits at the beginning of the hint and focus on the exact 48 bits.
        int shiftRight = 16 - bitOffset;
        clue >>>= shiftRight;
            // Mask to keep only 48 bits
        //Removes all bits to the right of the 48 significant bits. This leaves us with an exact 48-bit hint.
        clue = clue & ((1L << 48) - 1);
        return clue;
    }



        public static long findTreasure(FileChannel map, TreasureMapDecoder decoder, long firstClue) throws IOException {
        long mapSizeInBits = map.size() * 8; //the length of the file in bits
        long currentClue = firstClue; //define the current clue to be the first clue that given as input
        long currentLocation = -1; //the index of the first clue
        int iterationCount = 0;
        while (true) {
                // The condition checks whether the number of iterations has crossed the limit of 1001.
            if (iterationCount++ > 1001) {
                throw new IllegalStateException("Infinite loop detected!");
            }
            long nextLocation = decoder.decodeClue(currentClue, currentLocation, mapSizeInBits);
                // Debugging output to check the clue and next location
            System.out.println("Current Clue: " + currentClue);
            System.out.println("Next Location: " + nextLocation);
                //A condition that checks if the decoder returned -1, indicating that the treasure was found!
            if (nextLocation == -1) {
                System.out.println("Treasure found! Clue: " + currentLocation);
                return currentLocation;
            }
                // A condition that checks whether the location of the next clue exceeds the map boundaries.
            if (nextLocation >= mapSizeInBits) {
                currentClue = 0; // All out-of-bound bits are zero
            } else {
                    // If the location of the next clue (nextLocation) is correct and does not exceed the map boundaries, and we have not reached the treasure, we proceed to read it.
                // The call to the readClue method uses the location (nextLocation) to read the new clue from the map.
                // The result (the new clue) is saved in currentClue for further decoding in the loop.
                // A debug message prints the new value of the read clue (currentClue).
                // This allows tracking the progress of reading and decoding the clues on the map.
                currentClue = readClue(map, nextLocation);
                System.out.println("New Clue Read: " + currentClue);
            }
                // Update the current location
            currentLocation = nextLocation;
        }
    }
}