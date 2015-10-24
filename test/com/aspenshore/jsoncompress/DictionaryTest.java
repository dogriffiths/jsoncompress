import java.util.*;
import java.util.regex.*;

import org.junit.Assert;
import org.junit.Test;

public class DictionaryTest {
    @Test
    public void canEncodeAKnownWord() {
        String s = "FOOD";
        String code = Dictionary.encode(s);
        String backAgain = Dictionary.decode(code);
        Assert.assertEquals(s, backAgain);
    }

    @Test
    public void canEncodeAnUnknownWord() {
        String s = "FJDHFKJDHFK";
        String code = Dictionary.encode(s);
        String backAgain = Dictionary.decode(code);
        Assert.assertEquals(s, backAgain);
    }

    @Test
    public void ignoresLowerCase() {
        String s = "hello";
        String code = Dictionary.encode(s);
        String backAgain = Dictionary.decode(code);
        Assert.assertEquals(s, code);
        Assert.assertEquals(s, backAgain);
    }

    @Test
    public void findWords() {
        String str = "I CONTAIN SOME WORDS IN THE PATTERN AND Hello THERE SOMETHING";
        String result = shorten(str);
        System.err.println(result);
        String result2 = lengthen(result);
        System.err.println("Now result2 = " + result2);
    }

    public String lengthen(String result2) {
        Pattern shortCodesPattern = Pattern.compile("<[A-M]");
        List<String> shortCodesInString = wordsForPattern(shortCodesPattern, result2);
        for (String w : shortCodesInString) {
            String theWord = Dictionary.decode(w);
            result2 = result2.replaceAll(w, theWord);
        }
        Pattern longCodesPattern = Pattern.compile("<[N-Z0-9][A-Z0-9]");
        List<String> longCodesInString = wordsForPattern(longCodesPattern, result2);
        for (String w : longCodesInString) {
            String theWord = Dictionary.decode(w);
            result2 = result2.replaceAll(w, theWord);
        }
        return result2;
    }

    public String shorten(String str) {
        Pattern wordsPattern = Pattern.compile("[A-Z][A-Z][A-Z][A-Z]*");
        List<String> words = wordsForPattern(wordsPattern, str);
        String result = str;
        for (String w : words) {
            String code = Dictionary.encode(w);
            result = result.replaceAll(w, code);
        }
        return result;
    }

    public List<String> wordsForPattern(Pattern p, String str) {
        Matcher m = p.matcher(str);
        List<String> words = new ArrayList<String>();
        while(m.find()) {
            words.add(m.group(0));
        }

        Comparator<String> byLength = new Comparator<String>() {
                @Override
                public int compare(String a, String b) {
                    return b.length() - a.length();
                }
            };
        Collections.sort(words, byLength);
        return words;
    }
}
