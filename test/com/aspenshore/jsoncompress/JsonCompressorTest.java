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

    /*
class BitString {
    class BitString(byte b) {
    }
    
    void append(BitString s) {
    }

    int length() {
        return 0;
    }

    BitString last7Bits() {
        return null;
    }

    BitString allExceptLastByte() {
        return null;
    }

    int valueOf() {
        return 0;
    }
}

     */
    @Test
    public void canCreateABitStringFromBytes() {
        byte[] b = new byte[]{0x01, 0x02};
        BitString bs = new BitString(b);
        Assert.assertEquals(16, bs.length());
        Assert.assertEquals(0x0102, bs.valueOf());
    }
}
