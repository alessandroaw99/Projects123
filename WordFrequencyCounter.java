import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WordFrequencyCounter {

    private Map<String, Integer> wordFrequencies;

    public WordFrequencyCounter() {
        wordFrequencies = new HashMap<>();
    }

    public void countWords(List<String> words) {
        for (String word : words) {
            wordFrequencies.merge(word, 1, Integer::sum);
        }
    }

    public Map<String, Integer> getWordFrequencies() {
        return Collections.unmodifiableMap(wordFrequencies);
    }

}
