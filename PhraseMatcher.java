import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PhraseMatcher {

    private Map<String, Integer> phraseCounts;

    public PhraseMatcher() {
        phraseCounts = new HashMap<String, Integer>();
    }

    public void matchPhrases(List<String> phrases, List<String> fileContents) {
        for (String phrase : phrases) {
            int count = 0;
            for (String content : fileContents) {
                int index = 0;
                while (index != -1) {
                    index = content.indexOf(phrase, index);
                    if (index != -1) {
                        count++;
                        index += phrase.length();
                    }
                }
            }
            phraseCounts.put(phrase, count);
        }
    }

    public Map<String, Integer> getPhraseCounts() {
        return phraseCounts;
    }
}

