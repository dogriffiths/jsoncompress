import java.math.BigInteger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.junit.Assert;
import org.junit.Test;

public class JsonCompressorTest {

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

    @Test
    public void canCompressTextAndBack() {
        String s = "abcdefghijklThis is a really long, long sentence that I am writing. Will it work? I can only really tell by running the test";
        JsonCompressor jsonCompressor = new JsonCompressor();
        byte[] compressed = jsonCompressor.compress(s.getBytes());
        byte[] expanded = jsonCompressor.expand(compressed);
        Assert.assertEquals(s, new String(expanded));
    }

    private static void dump(byte[] array) {
        for (int i = 0; i < array.length; i++) {
            System.err.print(toBinary(array[i]) + " ");
        }
        System.err.println("");
    }

    private static void dump(byte[] array, int sectionLength) {
        String bits = bits(array);
        while(bits.length() > 0) {
            if (sectionLength == 8) {
                System.err.print(" ");
            }
            if (sectionLength == 7) {
                System.err.print("  ");
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

    @Test
    public void canEncodeASimpleObject() {
        JsonCompressor jsonCompressor = new JsonCompressor();
        assertWalkValid(jsonCompressor, "{\"a\":\"1\"}", "a>1", "{\"a\":\"1\"}");
        assertWalkValid(jsonCompressor, "{\"a\":\"1\",\"b\":\"2\"}", "a>1>b>2", "{\"a\":\"1\",\"b\":\"2\"}");
    }
    
    private void assertWalkValid(JsonCompressor jsonCompressor, String json, String expectedWalk, String expectedJson) {
        String actualWalk = jsonCompressor.walkFormat(json);
        Assert.assertEquals(expectedWalk, actualWalk);
        String actualJson = jsonCompressor.unwalkFormat(actualWalk);
        JSONObject actual = new JSONObject(actualJson);
        JSONObject expected = new JSONObject(expectedJson);
        Assert.assertEquals(expected.toString(), actual.toString());
    }

    private static String toBinary(byte sourceByte) {
        return String.format("%8s", Integer.toBinaryString(sourceByte & 0xFF)).replace(' ', '0');
    }    
}
