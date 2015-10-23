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
        byte[] b3 = new byte[]{0x01, 0x02, -1};
        BitString bs3 = new BitString(b3, 23);
        Assert.assertEquals(23, bs3.length());
        Assert.assertEquals(0x817f, bs3.valueOf());
        byte[] b4 = new byte[]{0x01, 0x02, -1};
        BitString bs4 = new BitString(b4, 17);
        Assert.assertEquals(17, bs4.length());
        Assert.assertEquals(0x205, bs4.valueOf());
    }

    @Test
    public void canGetLast7Bits() {
        BitString b = new BitString(new byte[]{0x01, 0x02, 0x7f});
        Assert.assertEquals(0x7f, b.last7Bits());
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
        BitString b8 = new BitString(new byte[]{0x01, 0x01, 0x7f}, 23);
        Assert.assertEquals(0x3f, b8.last7Bits());
    }

    @Test
    public void canGetAllExceptLastByte() {
        BitString b0 = new BitString(new byte[]{0x01, 0x02, -1});
        b0.removeLastByte();
        Assert.assertEquals(0x0102, b0.valueOf());
        BitString b1 = new BitString(new byte[]{0x01, 0x02, 0x03}, 23);
        b1.removeLastByte();
        Assert.assertEquals(0x81, b1.valueOf());
    }
}
