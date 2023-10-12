import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileProcessor {
    
    private File file;
    private BufferedReader reader;

    public FileProcessor(File file) throws FileNotFoundException {
        this.file = file;
        this.reader = new BufferedReader(new FileReader(file));
    }

    public List<String> readLines() throws IOException {
        List<String> lines = new ArrayList<>();
        String line;
        while ((line = reader.readLine()) != null) {
            lines.add(line);
        }
        return lines;
    }

    public void close() throws IOException {
        if (reader != null) {
            reader.close();
        }
    }
}
