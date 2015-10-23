import java.math.BigInteger;

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
        byte[] b2 = new byte[]{0x01, 0x02, -1};
        BitString bs2 = new BitString(b2, 18);
        Assert.assertEquals(18, bs2.length());
        Assert.assertEquals(0x40b, bs2.valueOf());
    }

    @Test
    public void canGetLast7Bits() {
        BitString b0 = new BitString(new byte[]{0x01, 0x02, -1});
        Assert.assertEquals(0x7f, b0.last7Bits());
        BitString b1 = new BitString(new byte[]{0x01, 0x02, -1}, 20);
        Assert.assertEquals(0x2f, b1.last7Bits());
        BitString b2 = new BitString(new byte[]{0x01, 0x02, -1}, 23);
        Assert.assertEquals(0x7f, b2.last7Bits());
        BitString b3 = new BitString(new byte[]{0x01, 0x02, -1}, 22);
        Assert.assertEquals(0x3f, b3.last7Bits());
        BitString b4 = new BitString(new byte[]{0x01, 0x02, -1}, 21);
        Assert.assertEquals(0x5f, b4.last7Bits());
        BitString b5 = new BitString(new byte[]{0x01, 0x02, -1}, 19);
        Assert.assertEquals(0x17, b5.last7Bits());
        BitString b6 = new BitString(new byte[]{0x01, 0x02, -1}, 18);
        Assert.assertEquals(0xb, b6.last7Bits());
        BitString b7 = new BitString(new byte[]{0x01, 0x02, -1}, 17);
        Assert.assertEquals(0x5, b7.last7Bits());
    }

    @Test
    public void canGetAllExceptLastByte() {
        /*        BitString b0 = new BitString(new byte[]{0x01, 0x02, -1});
        b0.removeLastByte();
        Assert.assertEquals(0x0102, b0.valueOf());*/
        /*        BitString b1 = new BitString(new byte[]{0x01, 0x02, 0x03}, 23);
        b1.removeLastByte();
        Assert.assertEquals(0x81, b1.valueOf());*/
    }
}
