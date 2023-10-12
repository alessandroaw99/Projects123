import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

public class PlagiarismChecker extends JFrame implements ActionListener {

    private static final long serialVersionUID = 1L;
    private static final int MIN_PHRASE_LENGTH = 3;

    private List<Map<String, Integer>> wordFrequencies;
    private double[][] phraseMatches;
    private JTextArea outputArea;
    private JTextField phraseLengthField;
    private JButton runButton;
    private JFileChooser fileChooser;

    public PlagiarismChecker() {
        super("Plagiarism Checker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel controlPanel = new JPanel();
        phraseLengthField = new JTextField(10);
        phraseLengthField.setText(Integer.toString(MIN_PHRASE_LENGTH));
        controlPanel.add(phraseLengthField);
        runButton = new JButton("Run Check");
        runButton.addActionListener(this);
        controlPanel.add(runButton);
        JButton selectFilesButton = new JButton("Select Files");
        selectFilesButton.addActionListener(this);
        controlPanel.add(selectFilesButton);
        add(controlPanel, BorderLayout.NORTH);

        outputArea = new JTextArea(25, 80);
        outputArea.setEditable(false);
        add(new JScrollPane(outputArea), BorderLayout.CENTER);

        fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.setFileFilter(new FileNameExtensionFilter("Text files", "txt"));

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        new PlagiarismChecker();
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == runButton) {
            int phraseLength = Integer.parseInt(phraseLengthField.getText());
            runCheck(phraseLength);
        } else {
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                // User selected some files
                try {
                    String[] filenames = Arrays.stream(fileChooser.getSelectedFiles())
                            .map(file -> file.getAbsolutePath())
                            .toArray(String[]::new);
                    String msg = String.format("Selected %d files: %s", filenames.length, String.join(", ", filenames));
                    JOptionPane.showMessageDialog(this, msg);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error selecting files: " + ex.getMessage());
                }
            } else {
                // User cancelled file selection
            }
        }
    }

    private void runCheck(int phraseLength) {
        outputArea.setText("Running check...\n");
    
        // Read in all the files and store their contents
        List<String> fileContents = new ArrayList<>();
        for (File file : fileChooser.getSelectedFiles()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                    sb.append("\n");
                }
                fileContents.add(sb.toString());
            } catch (IOException e) {
                outputArea.append("Error reading file: " + e.getMessage() + "\n");
            }
        }
    
        // Calculate the phrase matches for each pair of files
        int numFiles = fileChooser.getSelectedFiles().length;
        phraseMatches = new double[numFiles][numFiles];
        for (int i = 0; i < numFiles; i++) {
            for (int j = i + 1; j < numFiles; j++) {
                double match = calculatePhraseMatches(fileContents.get(i), fileContents.get(j), phraseLength);
                phraseMatches[i][j] = match;
                phraseMatches[j][i] = match;
            }
        }
    
        // Output the results
        StringBuilder result = new StringBuilder();
        result.append("Results:\n");
        for (int i = 0; i < numFiles; i++) {
            result.append(fileChooser.getSelectedFiles()[i].getName());
            result.append(": ");
            for (int j = 0; j < numFiles; j++) {
                result.append(String.format("%.2f", phraseMatches[i][j]));
                result.append("\t");
            }
            result.append("\n");
        }
        outputArea.append(result.toString());
    }
    
    private double calculatePhraseMatches(String file1Content, String file2Content, int phraseLength) {
        // Split each file's contents into phrases and count their occurrences
        Map<String, Integer>[] phraseCounts = new Map[2];
        for (int i = 0; i < 2; i++) {
            phraseCounts[i] = new HashMap<>();
            String content = (i == 0) ? file1Content : file2Content;
            String[] words = content.split("\\s+");
            for (int j = 0; j < words.length - phraseLength + 1; j++) {
                String phrase = String.join(" ", Arrays.copyOfRange(words, j, j + phraseLength));
                if (phraseCounts[i].containsKey(phrase)) {
                    phraseCounts[i].put(phrase, phraseCounts[i].get(phrase) + 1);
                } else {
                    phraseCounts[i].put(phrase, 1);
                }
            }
        }
    
        // Calculate the Jaccard similarity between the two sets of phrases
        int intersection = 0;
        int union = 0;
        for (String phrase : phraseCounts[0].keySet()) {
            if (phraseCounts[1].containsKey(phrase)) {
                intersection += Math.min(phraseCounts[0].get(phrase), phraseCounts[1].get(phrase));
            }
            union += phraseCounts[0].get(phrase);
        }
        for (String phrase : phraseCounts[1].keySet()) {
            union += phraseCounts[1].get(phrase);
        }
        return (double) intersection / union;
    }
}    
           
