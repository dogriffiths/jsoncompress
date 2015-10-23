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
}
