import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RecursiveTextSplitter {
    private List<String> separators;
    private boolean keepSeparator;
    private int chunkSize;
    private int chunkOverlap;

    public RecursiveTextSplitter(List<String> separators, boolean keepSeparator, int chunkSize, int chunkOverlap) {
        this.separators = separators;
        this.keepSeparator = keepSeparator;
        this.chunkSize = chunkSize;
        this.chunkOverlap = chunkOverlap;
    }

    private List<String> splitText(String text, List<String> separators) {
        List<String> finalChunks = new ArrayList<>();
        // Get appropriate separator to use
        String separator = separators.get(separators.size() - 1);
        List<String> newSeparators = new ArrayList<>();
        for (int i = 0; i < separators.size(); i++) {
            String s = separators.get(i);
            String regexSeparator = this.keepSeparator ? s : Pattern.quote(s);
            if (s.equals("")) {
                separator = s;
                break;
            }
            Pattern pattern = Pattern.compile(regexSeparator);
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                separator = s;
                newSeparators = separators.subList(i + 1, separators.size());
                break;
            }
        }

        String regexSeparator = this.keepSeparator ? separator : Pattern.quote(separator);
        String[] splits = text.split(regexSeparator);

        List<String> goodSplits = new ArrayList<>();
        for (String s : splits) {
            if (s.length() < this.chunkSize) {
                goodSplits.add(s);
            } else {
                if (!goodSplits.isEmpty()) {
                    String mergedText = String.join(separator, goodSplits);
                    finalChunks.add(mergedText);
                    goodSplits.clear();
                }
                if (newSeparators.isEmpty()) {
                    finalChunks.add(s);
                } else {
                    List<String> otherInfo = splitText(s, newSeparators);
                    finalChunks.addAll(otherInfo);
                }
            }
        }

        if (!goodSplits.isEmpty()) {
            String mergedText = String.join(separator, goodSplits);
            finalChunks.add(mergedText);
        }

        return finalChunks;
    }

    public List<String> splitText(String text) {
        return splitText(text, this.separators);
    }

    public static void main(String[] args) {
        List<String> separators = List.of("\n\n", "\n", " ", "");
        RecursiveTextSplitter splitter = new RecursiveTextSplitter(separators, true, 4000, 200);

        String inputText = "Your input text goes here...";
        List<String> resultChunks = splitter.splitText(inputText);

        for (String chunk : resultChunks) {
            System.out.println(chunk);
        }
    }
}
