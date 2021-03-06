package uk.co.blackpepper.jsoncompress;

import java.math.BigInteger;
import java.util.zip.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class JsonCompressorTest {

    @Test
    public void canExpand6SixBytes() {
        byte[] bytes = new byte[]{-1, -1, -1, -1, -1, -1};
        JsonCompressor jsonCompressor = new JsonCompressor();
        byte[] expanded = jsonCompressor.unpack(bytes, 6);
        Assert.assertEquals(8, expanded.length);
        Assert.assertEquals(0x3f, expanded[0] & 0xff);
        Assert.assertEquals(0x3f, expanded[1] & 0xff);
        Assert.assertEquals(0x3f, expanded[2] & 0xff);
        Assert.assertEquals(0x3f, expanded[3] & 0xff);
        Assert.assertEquals(0x3f, expanded[4] & 0xff);
        Assert.assertEquals(0x3f, expanded[5] & 0xff);
        Assert.assertEquals(0x3f, expanded[6] & 0xff);
        Assert.assertEquals(0x3f, expanded[7] & 0xff);
    }

    @Test
    public void canCompressEightBytes() {
        byte[] bytes = new byte[]{0x7f, 0x7f, 0x7f, 0x7f, 0x7f, 0x7f, 0x7f, 0x7f};
        JsonCompressor jsonCompressor = new JsonCompressor();
        byte[] compress = jsonCompressor.pack(bytes, 7);
        Assert.assertEquals(7, compress.length);
        Assert.assertEquals(0xff, compress[0] & 0xff);
        Assert.assertEquals(0xff, compress[1] & 0xff);
        Assert.assertEquals(0xff, compress[2] & 0xff);
        Assert.assertEquals(0xff, compress[3] & 0xff);
        Assert.assertEquals(0xff, compress[4] & 0xff);
        Assert.assertEquals(0xff, compress[5] & 0xff);
        Assert.assertEquals(0xff, compress[6] & 0xff);
    }

    @Test
    public void canCompress6EightBytes() {
        byte[] bytes = new byte[]{0x3f, 0x3f, 0x3f, 0x3f, 0x3f, 0x3f, 0x3f, 0x3f};
        JsonCompressor jsonCompressor = new JsonCompressor();
        byte[] compress = jsonCompressor.pack(bytes, 6);
        Assert.assertEquals(6, compress.length);
        Assert.assertEquals(0xff, compress[0] & 0xff);
        Assert.assertEquals(0xff, compress[1] & 0xff);
        Assert.assertEquals(0xff, compress[2] & 0xff);
        Assert.assertEquals(0xff, compress[3] & 0xff);
        Assert.assertEquals(0xff, compress[4] & 0xff);
        Assert.assertEquals(0xff, compress[5] & 0xff);
    }

    @Test
    public void canExpandBytes() {
        byte[] bytes = new byte[]{(byte)(0x81 & 0xff), 0};
        JsonCompressor jsonCompressor = new JsonCompressor();
        byte[] expanded = jsonCompressor.unpack(bytes, 7);
        Assert.assertEquals(0x40, expanded[0] & 0xff);
        Assert.assertEquals(0x40, expanded[1] & 0xff);
    }

    @Test
    public void canExpandSevenBytes() {
        byte[] bytes = new byte[]{-1, -1, -1, -1, -1, -1, -1};
        JsonCompressor jsonCompressor = new JsonCompressor();
        byte[] expanded = jsonCompressor.unpack(bytes, 7);
        Assert.assertEquals(8, expanded.length);
        Assert.assertEquals(0x7f, expanded[0] & 0xff);
        Assert.assertEquals(0x7f, expanded[1] & 0xff);
        Assert.assertEquals(0x7f, expanded[2] & 0xff);
        Assert.assertEquals(0x7f, expanded[3] & 0xff);
        Assert.assertEquals(0x7f, expanded[4] & 0xff);
        Assert.assertEquals(0x7f, expanded[5] & 0xff);
        Assert.assertEquals(0x7f, expanded[6] & 0xff);
        Assert.assertEquals(0x7f, expanded[7] & 0xff);
    }

    //    @Test
    public void compressAndExpandCorrectly() {
        //        byte[] bytes = new byte[]{0x61, 0x62, 0x63, 0x64, 0x65, 0x66, 0x67, 0x68, 0x69};
        byte[] bytes = new byte[]{0x61, 0x62, 0x63, 0x64, 0x65, 0x66, 0x67, 0x68, 0x69, 0x70};
        JsonCompressor jsonCompressor = new JsonCompressor();
        byte[] compressed = jsonCompressor.pack(bytes, 7);
        Assert.assertEquals(9, compressed.length);
        byte[] expanded = jsonCompressor.unpack(compressed, 7);
        dump(bytes, 8);
        dump(compressed, 7);
        dump(expanded, 8);
    }

    //@Test
    public void compress6AndExpand6Correctly() {
        //        byte[] bytes = new byte[]{0x41, 0x42, 0x43, 0x44, 0x45, 0x46, 0x47, 0x48, 0x49, 0x50};
        byte[] bytes = new byte[]{0x21, 0x22, 0x23, 0x24, 0x25, 0x26, 0x27, 0x28, 0x29, 0x30, 0x31, 0x32, 0x33, 0x34};
        JsonCompressor jsonCompressor = new JsonCompressor();
        byte[] compressed = jsonCompressor.pack(bytes, 6);
        byte[] expanded = jsonCompressor.unpack(compressed, 6);
        dump(bytes, 8);
        dump(compressed, 6);
        dump(expanded, 8);
    }

    @Test
    public void canCompress6TextAndBack() {
        String s = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789;-=+";
        JsonCompressor jsonCompressor = new JsonCompressor();
        byte[] compressed = jsonCompressor.compress6AndDec(s.getBytes());
        byte[] expanded = jsonCompressor.expand6AndInc(compressed);
        Assert.assertEquals(s, new String(expanded));
    }

    @Test
    public void canWalkAndUnwalkJsonWithSymbols() {
        JsonCompressor jsonCompressor = new JsonCompressor();
        String json = "{\"c\":[\"d>>1\",\"e^\",\"f\"],\"a\":\"1>2 Hello;+World;Test+Here*With*+A*Set+Of\"}";
        String walkFormat = jsonCompressor.walkFormat(json);
        Assert.assertEquals("c>*d;>;>1>e;^>f^>a>1;>2 Hello;;;+World;;Test;+Here;*With;*;+A;*Set;+Of", walkFormat);
        String unwalkFormat = jsonCompressor.unwalkFormat(walkFormat);
        Assert.assertEquals(normalizeJson(json), normalizeJson(unwalkFormat));
        byte[] compressed = jsonCompressor.compressJson(json);
        String expanded = jsonCompressor.expandJson(compressed);
        Assert.assertEquals(normalizeJson(json), normalizeJson(expanded));
    }

    @Test
    @Ignore("Still gets confused if text looks like a word replacement, e.g. <sy")
    public void canWalkAndUnwalkJsonWithSymbolsWithWordExpansionGettingConfused() {
        JsonCompressor jsonCompressor = new JsonCompressor();
        String json = "{\"c\":[\"d>>1\",\"e^\",\"f\"],\"a\":\"1>2 Hello;+World;Test+Here*With*+A*Set+Of <sym<BOls\"}";
        String walkFormat = jsonCompressor.walkFormat(json);
        Assert.assertEquals("c>*d;>;>1>e;^>f^>a>1;>2 Hello;;;+World;;Test;+Here;*With;*;+A;*Set;+Of ;<sym;<BOls", walkFormat);
        String unwalkFormat = jsonCompressor.unwalkFormat(walkFormat);
        Assert.assertEquals(json, unwalkFormat);
        byte[] compressed = jsonCompressor.compressJson(json);
        String expanded = jsonCompressor.expandJson(compressed);
        Assert.assertEquals(json, expanded);
    }

    @Test
    public void canCompressTextAndBack() {
        String s = "abcdefghijklThis is a really long, long sentence that I am writing. Will it work? I can only really tell by running the test";
        JsonCompressor jsonCompressor = new JsonCompressor();
        byte[] compressed = jsonCompressor.pack(s.getBytes(), 7);
        byte[] expanded = jsonCompressor.unpack(compressed, 7);
        Assert.assertEquals(s, new String(expanded));
    }

    @Test
    public void canEncodeAnArray() {
        JsonCompressor jsonCompressor = new JsonCompressor();
        assertWalkValid(jsonCompressor, "[\"a\",\"b\"]", "*a>b", "[\"a\",\"b\"]");
        assertWalkValid(jsonCompressor, "[\"a\",\"b\",\"c\"]", "*a>b>c", "[\"a\",\"b\",\"c\"]");
        assertWalkValid(jsonCompressor, "[\"a\"]", "*a", "[\"a\"]");
        assertWalkValid(jsonCompressor, "[\"a\",[\"b\"]]", "*a>*b", "[\"a\",[\"b\"]]");
        assertWalkValid(jsonCompressor, "[\"a\",[\"b\"],\"c\"]", "*a>*b^>c", "[\"a\",[\"b\"],\"c\"]");
        assertWalkValid(jsonCompressor, "[\"a\",[\"b\",\"c\"]]", "*a>*b>c", "[\"a\",[\"b\",\"c\"]]");
    }

    @Test
    public void bugDontPutZeroesAtTheEnd() {
        JsonCompressor jsonCompressor = new JsonCompressor();
        String walkFormat = "Rcr>6>Times*08:00>12:00>16:00^Ntnt>Daily>Ntvl>3>Title>Gemtuzumab ozogamicin>Info>Take with food";
        byte[] compress = jsonCompressor.pack(walkFormat.getBytes(), 7);
        byte[] expand = jsonCompressor.unpack(compress, 7);
        Assert.assertEquals(walkFormat, new String(expand));
    }

    @Test
    public void canCompressAndExpandAString() {
        String s = "ABCDEFGHI";
        JsonCompressor jsonCompressor = new JsonCompressor();
        byte[] compress = jsonCompressor.pack(s.getBytes(), 7);
        byte[] expand = jsonCompressor.unpack(compress, 7);
        String result = new String(expand);
        Assert.assertEquals(s, result);
    }

    //@Test
    public void canWalkAnArrayOfObjects() {
        JsonCompressor jsonCompressor = new JsonCompressor();
        String s = normalizeJson("{\"a\":[{\"e\":\"f\"},{\"g\":\"g\"}]}");
        System.err.println("s = " + s);
        String walked = jsonCompressor.walkFormat(s);
        System.err.println("walked = " + walked);
        Assert.assertEquals("a*+e>f^+g>g", walked);
    }

    @Test
    public void compressAndExpandWithLotsOfData() throws Exception {
        JsonCompressor jsonCompressor = new JsonCompressor();
        for (int run = 0; run < 200; run++) {
            byte[] data = new byte[run];
            for (int i = 0; i < data.length; i++) {
                int val = (int)((Math.random() * 127));
                data[i] = (byte)(0x7f & val);
            }
            // Make sure we don't end with a zero, because we trim those
            if (data.length > 0) {
                while (data[data.length - 1] == 0) {
                    int val = (int)((Math.random() * 127));
                    data[data.length - 1] = (byte)(0x7f & val);
                }
            }
            byte[] compressed = jsonCompressor.pack(data, 7);
            byte[] expanded = jsonCompressor.unpack(compressed, 7);
            for (int i = 0; i < data.length; i++) {
                try {
                    Assert.assertEquals("Failed at position " + i, data[i], expanded[i]);
                } catch(Exception e) {
                    System.err.println("Failed for:\n");
                    dumpHex(data);
                    throw e;
                }
            }
        }
    }

    @Test
    public void compress6AndExpand6WithLotsOfData() throws Exception {
        JsonCompressor jsonCompressor = new JsonCompressor();
        for (int run = 0; run < 200; run++) {
            byte[] data = new byte[run];
            for (int i = 0; i < data.length; i++) {
                int val = (int)((Math.random() * 64));
                data[i] = (byte)(0x7f & val);
            }
            // Make sure we don't end with a zero, because we trim those
            if (data.length > 0) {
                while (data[data.length - 1] == 0) {
                    int val = (int)((Math.random() * 64));
                    data[data.length - 1] = (byte)(0x7f & val);
                }
            }
            byte[] compressed = jsonCompressor.pack(data, 6);
            byte[] expanded = jsonCompressor.unpack(compressed, 6);
            for (int i = 0; i < data.length; i++) {
                try {
                    Assert.assertEquals("Failed at position " + i, data[i], expanded[i]);
                } catch(Exception e) {
                    System.err.println("Failed for:\n");
                    dumpHex(data);
                    throw e;
                }
            }
        }
    }

    @Test
    public void canSquishJson() {
        String s1 = "{\"Ntnt\":\"Daily\",\"Ntvl\":\"1\",\"Rcr\":\"6\","
            + "\"Title\":\"Paracetamol\","
            + "\"Test\":\"For example, 100 bytes on a tag is not even enough storage to record the contents of this sentence.\"}";
        String s = normalizeJson(s1);
        JsonCompressor jsonCompressor = new JsonCompressor();
        byte[] compress = jsonCompressor.compressJson(s);
        Assert.assertEquals(168, s.length());
        Assert.assertEquals(95, compress.length);
        String result = jsonCompressor.expandJson(compress);
        Assert.assertEquals(s, normalizeJson(result));
    }

    @Test
    public void canHaveAnArrayOfMaps() {
        JsonCompressor jsonCompressor = new JsonCompressor();
        assertWalkValid(jsonCompressor, "[{\"a\":\"b\"}]", "*+a>b", "[{\"a\":\"b\"}]");
    }

    @Test
    public void canEncodeASimpleObject() {
        JsonCompressor jsonCompressor = new JsonCompressor();
        assertWalkValid(jsonCompressor, "{\"a\":\"1\"}", "a>1", "{\"a\":\"1\"}");
        assertWalkValid(jsonCompressor, "{\"a\":\"1\",\"b\":\"2\"}", "a>1>b>2", "{\"a\":\"1\",\"b\":\"2\"}");
    }

    @Test
    public void canEncodeAComplexObject() {
        JsonCompressor jsonCompressor = new JsonCompressor();
        assertWalkValid(jsonCompressor, "{\"a\":\"1\",\"a1\":{\"b\":\"2\"}}", "a>1>a1>+b>2", "{\"a1\":{\"b\":\"2\"},\"a\":\"1\"}");
        assertWalkValid(jsonCompressor, "{\"a\":\"1\",\"a1\":{\"b\":\"2\"},\"c\":\"3\"}", "a>1>a1>+b>2^>c>3", "{\"a1\":{\"b\":\"2\"},\"a\":\"1\",\"c\":\"3\"}");
        assertWalkValid(jsonCompressor, "{\"a\":\"1\",\"a1\":{\"b\":\"2\",\"b1\":{\"c\":\"3\"}}}", "a>1>a1>+b>2>b1>+c>3", "{\"a1\":{\"b\":\"2\",\"b1\":{\"c\":\"3\"}},\"a\":\"1\"}");
        assertWalkValid(jsonCompressor, "{\"a\":\"1\",\"a0\":{\"b\":\"2\",\"b1\":{\"c\":\"3\"}},\"a1\":{\"d\":\"4\"}}", "a>1>a0>+b>2>b1>+c>3^^>a1>+d>4", "{\"a1\":{\"d\":\"4\"},\"a\":\"1\",\"a0\":{\"b\":\"2\",\"b1\":{\"c\":\"3\"}}}");
    }

    @Test
    public void checkADictionaryHelp() {
        String prototype = "{\n"+
            "   \"type\":\"record\",\n"+
            "   \"location\":\"somewhere\",\n"+
            "   \"filename\":\"bill.mp3\",\n"+
            "   \"sample_rate\":\"8000\",\n"+
            "   \"encoding_rate\":\"3000\",\n"+
            "   \"audio_type\":\"mp3\",\n"+
            "   \"use_video\":\"true\",\n"+
            "   \"schedule\":[\"09:00\",\"12:00\"],\n"+
            "   \"stereo\":\"true\"\n"+
            "}\n";
        String s1 = "{\n"+
            "   \"type\":\"record\",\n"+
            "   \"location\":\"in the boardroom\",\n"+
            "   \"filename\":\"fred.aac\",\n"+
            "   \"sample_rate\":\"48000\",\n"+
            "   \"encoding_rate\":\"320000\",\n"+
            "   \"audio_type\":\"aac\",\n"+
            "   \"use_video\":\"false\",\n"+
            "   \"schedule\":[\"11:00\",\"14:00\",\"18:00\"],\n"+
            "   \"stereo\":\"false\"\n"+
            "}\n";
        String s = normalizeJson(s1);
        JsonCompressor compressorWithPrototype = new JsonCompressor(prototype);
        JsonCompressor compressor = new JsonCompressor();
        byte[] compress = compressor.compressJson(s);
        byte[] compressWithPrototype = compressorWithPrototype.compressJson(s);
        Assert.assertEquals(209, s.length());
        Assert.assertEquals(108, compress.length);
        Assert.assertEquals(75, compressWithPrototype.length);
        String result = compressor.expandJson(compress);
        Assert.assertEquals(s, normalizeJson(result));
        String resultWithPrototype = compressorWithPrototype.expandJson(compressWithPrototype);
        Assert.assertEquals(normalizeJson(s), normalizeJson(resultWithPrototype));
    }

    @Test
    public void compressionMetrics() {
        // Smaz compressed '{"t":"This is a small string"}' by 28%
        Assert.assertEquals(47, getCompressedByPerCent("{\"t\":\"This is a small string\"}"));

        // Smaz compressed '{"t":"foobar"}' by 0%
        Assert.assertEquals(50, getCompressedByPerCent("{\"t\":\"foobar\"}"));

        // Smaz compressed '{"t":"the end"}' by 12%
        Assert.assertEquals(54, getCompressedByPerCent("{\"t\":\"the end\"}"));

        // Smaz enlarged '{"t":"not-a-g00d-Exampl333"}' by 16%
        Assert.assertEquals(36, getCompressedByPerCent("{\"t\":\"not-a-g00d-Exampl333\"}"));

        // Smaz compressed '{"t":"Smaz is a simple compression library"}' by 26%
        Assert.assertEquals(44, getCompressedByPerCent("{\"t\":\"Smaz is a simple compression library\"}"));

        // Smaz compressed '{"t":"Nothing is more difficult, and therefore more precious, than to be able to decide"}' by 41%
        Assert.assertEquals(41, getCompressedByPerCent("{\"t\":\"Nothing is more difficult, and therefore more precious, than to be able to decide\"}"));

        // Smaz compressed '{"t":"this is an example of what works very well with smaz"}' by 37%
        Assert.assertEquals(44, getCompressedByPerCent("{\"t\":\"this is an example of what works very well with smaz\"}"));

        // Smaz compressed '{"t":"1000 numbers 2000 will 10 20 30 compress very little"}' by 5%
        Assert.assertEquals(42, getCompressedByPerCent("{\"t\":\"1000 numbers 2000 will 10 20 30 compress very little\"}"));

        // Smaz compressed '{"t":"Nel mezzo del cammin di nostra vita, mi ritrovai in una selva oscura"}' by 26%
        Assert.assertEquals(28, getCompressedByPerCent("{\"t\":\"Nel mezzo del cammin di nostra vita, mi ritrovai in una selva oscura\"}"));

        // Smaz compressed '{"t":"Mi illumino di immenso"}' by 19%
        Assert.assertEquals(34, getCompressedByPerCent("{\"t\":\"Mi illumino di immenso\"}"));

        // Smaz compressed '{"t":"L'autore di questa libreria vive in Sicilia"}' by 19%
        Assert.assertEquals(28, getCompressedByPerCent("{\"t\":\"L'autore di questa libreria vive in Sicilia\"}"));

        // Smaz compressed '{"t":"http://google.com"}' by 28%
        Assert.assertEquals(48, getCompressedByPerCent("{\"t\":\"http://google.com\"}"));

        // Smaz compressed '{"t":"http://programming.reddit.com"}' by 33%
        Assert.assertEquals(49, getCompressedByPerCent("{\"t\":\"http://programming.reddit.com\"}"));

        // Smaz compressed '{"t":"http://github.com/antirez/smaz/tree/master"}' by 32%
        Assert.assertEquals(38, getCompressedByPerCent("{\"t\":\"`http://github.com/antirez/smaz/tree/master\"}"));
    }
    
    //
    // UTILITIES
    //

    private static int getCompressedByPerCent(String s) {
        JsonCompressor compressor = new JsonCompressor();
        byte[] result = compressor.compressJson(s);
        int compressedByPerCent = 100 - (100 * result.length / s.length());
        return compressedByPerCent;
    }
    
    private String normalizeJson(String s) {
        return (new JSONObject(s)).toString();
    }
    
    private void assertWalkValid(JsonCompressor jsonCompressor, String json, String expectedWalk, String expectedJson) {
        String actualWalk = jsonCompressor.walkFormat(json);
        Assert.assertEquals(expectedWalk, actualWalk);
        String actualJson = jsonCompressor.unwalkFormat(actualWalk);
        if (actualJson.startsWith("[")) {
            JSONArray actual = new JSONArray(actualJson);
            JSONArray expected = new JSONArray(expectedJson);
            Assert.assertEquals(expected.toString(), actual.toString());
        } else {
            JSONObject actual = new JSONObject(actualJson);
            JSONObject expected = new JSONObject(expectedJson);
            Assert.assertEquals(expected.toString(), actual.toString());
        }
    }

    private static void dump(byte[] array) {
        for (int i = 0; i < array.length; i++) {
            System.err.print(toBinary(array[i]) + " ");
        }
        System.err.println("");
    }

    private static void dumpHex(byte[] array) {
        System.err.print("new byte[]{");
        for (int i = 0; i < array.length; i++) {
            System.err.print(toHex(array[i]));
            if (i < array.length - 1) {
                System.err.print(", ");
            }
        }
        System.err.println("}");
    }

    private static void dump(byte[] array, int sectionLength) {
        System.err.println("");
        String bits = bits(array);
        while(bits.length() > 0) {
            if (sectionLength == 8) {
                System.err.print(" ");
            }
            if (sectionLength == 7) {
                System.err.print("  ");
            }
            if (sectionLength == 6) {
                System.err.print("   ");
            }
            int x = sectionLength;
            if (x > bits.length()) {
                x = bits.length();
            }
            System.err.print(bits.substring(0, x));
            bits = bits.substring(x);
        }
        System.err.println("");
    }
    
    private static String bits(byte[] array) {
        String result = "";
        for (int i = 0; i < array.length; i++) {
            result += toBinary(array[i]);
        }
        return result;
    }

    private static String toHex(int sourceByte) {
        return String.format("0x%2s", Integer.toHexString(sourceByte & 0xFF)).replace(' ', '0');
    }    

    private static String toBinary(int sourceByte) {
        return String.format("%8s", Integer.toBinaryString(sourceByte & 0xFF)).replace(' ', '0');
    }    
}
