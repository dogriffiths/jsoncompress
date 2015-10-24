import java.math.BigInteger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.junit.Assert;
import org.junit.Test;

public class JsonCompressorTest {

    @Test
    public void canExpand6SixBytes() {
        byte[] bytes = new byte[]{-1, -1, -1, -1, -1, -1};
        JsonCompressor jsonCompressor = new JsonCompressor();
        byte[] expanded = jsonCompressor.expand6(bytes);
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
        byte[] compress = jsonCompressor.compress(bytes);
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
        byte[] compress = jsonCompressor.compress6(bytes);
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
        byte[] expanded = jsonCompressor.expand(bytes);
        Assert.assertEquals(0x40, expanded[0] & 0xff);
        Assert.assertEquals(0x40, expanded[1] & 0xff);
    }

    @Test
    public void canExpandSevenBytes() {
        byte[] bytes = new byte[]{-1, -1, -1, -1, -1, -1, -1};
        JsonCompressor jsonCompressor = new JsonCompressor();
        byte[] expanded = jsonCompressor.expand(bytes);
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
        byte[] compressed = jsonCompressor.compress(bytes);
        Assert.assertEquals(9, compressed.length);
        byte[] expanded = jsonCompressor.expand(compressed);
        dump(bytes, 8);
        dump(compressed, 7);
        dump(expanded, 8);
    }

    //@Test
    public void compress6AndExpand6Correctly() {
        //        byte[] bytes = new byte[]{0x41, 0x42, 0x43, 0x44, 0x45, 0x46, 0x47, 0x48, 0x49, 0x50};
        byte[] bytes = new byte[]{0x21, 0x22, 0x23, 0x24, 0x25, 0x26, 0x27, 0x28, 0x29, 0x30, 0x31, 0x32, 0x33, 0x34};
        JsonCompressor jsonCompressor = new JsonCompressor();
        byte[] compressed = jsonCompressor.compress6(bytes);
        //        Assert.assertEquals(9, compressed.length);
        byte[] expanded = jsonCompressor.expand6(compressed);
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
    public void canCompressTextAndBack() {
        String s = "abcdefghijklThis is a really long, long sentence that I am writing. Will it work? I can only really tell by running the test";
        JsonCompressor jsonCompressor = new JsonCompressor();
        byte[] compressed = jsonCompressor.compress(s.getBytes());
        byte[] expanded = jsonCompressor.expand(compressed);
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
        byte[] compress = jsonCompressor.compress(walkFormat.getBytes());
        byte[] expand = jsonCompressor.expand(compress);
        Assert.assertEquals(walkFormat, new String(expand));
    }

    @Test
    public void canCompressAndExpandAString() {
        String s = "ABCDEFGHI";
        JsonCompressor jsonCompressor = new JsonCompressor();
        byte[] compress = jsonCompressor.compress(s.getBytes());
        byte[] expand = jsonCompressor.expand(compress);
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
    public void canSquishJson() {
        String s = normalizeJson("{\"menu\":{\"header\":\"SVG Viewer\",\"items\":[{\"id\":\"Open\"},{\"id\":\"OpenNew\",\"label\":\"Open New\"},{\"id\":\"ZoomIn\",\"label\":\"Zoom In\"},{\"id\":\"ZoomOut\",\"label\":\"Zoom Out\"},{\"id\":\"OriginalView\",\"label\":\"Original View\"},{\"id\":\"Quality\"},{\"id\":\"Pause\"},{\"id\":\"Mute\"},{\"id\":\"Find\",\"label\":\"Find...\"},{\"id\":\"FindAgain\",\"label\":\"Find Again\"},{\"id\":\"Copy\"},{\"id\":\"CopyAgain\",\"label\":\"Copy Again\"},{\"id\":\"CopySVG\",\"label\":\"Copy SVG\"},{\"id\":\"ViewSVG\",\"label\":\"View SVG\"},{\"id\":\"ViewSource\",\"label\":\"View Source\"},{\"id\":\"SaveAs\",\"label\":\"Save As\"},{\"id\":\"Help\"},{\"id\":\"About\",\"label\":\"About Adobe CVG Viewer...\"}]}}");
        JsonCompressor jsonCompressor = new JsonCompressor();
        byte[] compress = jsonCompressor.compressJson(s);
        System.err.println("JSON compressed from " + s.length() + " bytes to " + compress.length + " bytes");
        String result = jsonCompressor.expandJson(compress);
        Assert.assertEquals(s, result);
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


    //
    // UTILITIES
    //
    
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

    private static String toBinary(int sourceByte) {
        return String.format("%8s", Integer.toBinaryString(sourceByte & 0xFF)).replace(' ', '0');
    }    
}
