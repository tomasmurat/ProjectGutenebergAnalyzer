import java.io.Serializable;

// This class  stores the information about only one analyzed book
public class BookAnalysis implements Serializable {

    private String title;
    private int lineCount;
    private int wordCount;
    private int charCount;

    // Constructor
    public BookAnalysis(String title, int lineCount, int wordCount, int charCount) {
        this.title = title;
        this.lineCount = lineCount;
        this.wordCount = wordCount;
        this.charCount = charCount;
    }

    public String getTitle() {
        return title;
    }

    public int getLineCount() {
        return lineCount;
    }

    public int getWordCount() {
        return wordCount;
    }

    public int getCharCount() {
        return charCount;
    }

    // Makes it easier to print the object
    public String toString() {
        return "Title: " + title + "\nLines: " + lineCount + "\nWords: " + wordCount + "\nCharacters: " + charCount;
    }
}