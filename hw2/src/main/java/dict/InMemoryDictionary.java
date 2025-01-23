package dict;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import java.io.BufferedReader;
import java.io.BufferedWriter;

/**
 * Implements a persistent dictionary that can be held entirely in memory.
 * When flushed, it writes the entire dictionary back to a file.
 * <p>
 * The file format has one keyword per line:
 * <pre>word:def</pre>
 * <p>
 * Note that an empty definition list is allowed (in which case the entry would have the form: <pre>word:</pre>
 *
 * @author talm
 */
public class InMemoryDictionary extends TreeMap<String, String> implements PersistentDictionary {
    private static final long serialVersionUID = 1L; // (because we're extending a serializable class)
    private File file;
    public InMemoryDictionary(File dictFile) {
        // TODO: Implement constructor
        this.file = dictFile ;
    }

    @Override
    public void open() throws IOException {
        // TODO Auto-generated method stub
        this.clear();
        if (!file.exists()) {
            return;
        }
        try{
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String currentLine = reader.readLine();
            while(currentLine != null){
                String[] saperatorIndex = currentLine.split(":",2);
                String part1 = saperatorIndex[0];
                String part2 = "";
                    if(saperatorIndex.length == 2){
                        part2 = saperatorIndex[1];
                }
                this.put(part1, part2);
                currentLine = reader.readLine();
            }
            reader.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void close() throws IOException {
        // TODO Auto-generated method stub
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        for(Map.Entry<String,String> entry : this.entrySet()) {
                String Line = entry.getKey() + ":" + entry.getValue() ;
                writer.write(Line);
                writer.newLine();
            }
            writer.close();
        }
}
