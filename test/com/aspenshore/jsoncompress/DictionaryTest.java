package com.aspenshore.jsoncompress;

import java.util.*;
import java.util.regex.*;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class DictionaryTest {
    @Test
    public void canEncodeAKnownWord() {
        String s = "SIMPLY";
        String code = Dictionary.encode(s);
        Assert.assertEquals(code, "<!!");
        String backAgain = Dictionary.decode(code);
        Assert.assertEquals(s, backAgain);
    }

    @Test
    @Ignore("Currently replaces stuff even if the < is escaped")
    public void dontReplaceEscapeLessThan() {
        String s = ";<SYMBOL";
        String lengthen = Dictionary.lengthen(s);
        Assert.assertEquals(s, lengthen);
    }

    @Test
    public void doReplaceEscapeLessThanIfNotReallyEscaped() {
        String s = ";;<SYMBOL";
        String lengthen = Dictionary.lengthen(s);
        Assert.assertEquals(";;RATEMBOL", lengthen);
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
        String result = Dictionary.shorten(str);
        String result2 = Dictionary.lengthen(result);
    }
}
