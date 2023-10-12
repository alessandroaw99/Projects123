import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JTextArea;

public class FileReader {
    
    public void openFile(JTextArea outputArea) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        int result = fileChooser.showOpenDialog(null);
        
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            outputArea.append("Selected file: " + selectedFile.getAbsolutePath() + "\n");
            
            try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    outputArea.append(line + "\n");
                }
            } catch (IOException e) {
                outputArea.append("Error reading file: " + e.getMessage() + "\n");
            }
        }
    }
}
