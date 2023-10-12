import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main {
    private static final String[] filenames = {"file1.txt", "file2.txt", "file3.txt"};
    private static final int PHRASE_LENGTH = 3;

    public static void main(String[] args) {
        List<String> phrases = FileReader.readFiles(filenames);
        PlagiarismChecker checker = new PlagiarismChecker(phrases, PHRASE_LENGTH);

        List<Map<String, Double>> matchResults = checker.checkFiles(filenames);

        List<MatchResult> overallMatchResults = getOverallMatches(matchResults);
        for (MatchResult result : overallMatchResults) {
            System.out.printf("Files %s and %s have a plagiarism match percentage of %.2f%%%n",
                    result.getFile1Name(), result.getFile2Name(), result.getMatchPercentage());
        }

        for (String filename : filenames) {
            List<MatchResult> fileMatchResults = getFileMatches(matchResults, filename);
            for (MatchResult result : fileMatchResults) {
                System.out.printf("File %s has a plagiarism match of %.2f%% with file %s%n",
                        result.getFile1Name(), result.getMatchPercentage(), result.getFile2Name());
            }
        }
    }

    private static List<MatchResult> getOverallMatches(List<Map<String, Double>> matchResults) {
        List<MatchResult> overallMatches = new ArrayList<>();
        for (int i = 0; i < matchResults.size(); i++) {
            for (int j = i + 1; j < matchResults.size(); j++) {
                double matchPercentage = getMatchPercentage(matchResults.get(i), matchResults.get(j));
                overallMatches.add(new MatchResult(filenames[i], filenames[j], matchPercentage));
            }
        }
        return overallMatches;
    }

    private static List<MatchResult> getFileMatches(List<Map<String, Double>> matchResults, String filename) {
        List<MatchResult> fileMatches = new ArrayList<>();
        int fileIndex = getFilenameIndex(filename);
        for (int i = 0; i < matchResults.size(); i++) {
            if (i != fileIndex) {
                double matchPercentage = matchResults.get(i).get(filename);
                fileMatches.add(new MatchResult(filename, filenames[i], matchPercentage));
            }
        }
        return fileMatches;
    }

    private static double getMatchPercentage(Map<String, Double> file1Results, Map<String, Double> file2Results) {
        double totalMatchPercentage = 0;
        for (String phrase : file1Results.keySet()) {
            double file1Percentage = file1Results.get(phrase);
            double file2Percentage = file2Results.get(phrase);
            double phraseMatchPercentage = Math.min(file1Percentage, file2Percentage);
            totalMatchPercentage += phraseMatchPercentage;
        }
        return (totalMatchPercentage / file1Results.size()) * 100;
    }

    private static int getFilenameIndex(String filename) {
        for (int i = 0; i < filenames.length; i++) {
            if (filenames[i].equals(filename)) {
                return i;
            }
        }
        return -1;
    }
}
