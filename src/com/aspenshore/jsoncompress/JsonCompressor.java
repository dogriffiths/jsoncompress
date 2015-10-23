import java.util.ArrayList;
import java.util.List;

public class JsonCompressor {
    public byte[] compress(byte[] sourceBytes) {
        String input = "";
        for (byte sourceByte : sourceBytes) {
            input = toBinary(sourceByte).substring(1,8) + input;
        }
        List<Byte> results = new ArrayList<Byte>();
        while(input.length() > 0) {
            int len = input.length();
            int start = len - 8;
            if (start < 0) {
                start = 0;
            }
            String sevenBits = input.substring(start, len);
            input = input.substring(0, start);
            Integer integer = Integer.valueOf(sevenBits, 2);
            byte b = (byte)(integer & 0xff);
            results.add(b);
        }
        byte[] finalResult = new byte[results.size()];
        for (int i = 0; i < finalResult.length; i++) {
            finalResult[i] = results.get(i);
        }
        return finalResult;
    }

    public byte[] expand(byte[] sourceBytes) {
        String input = "";
        for (byte sourceByte : sourceBytes) {
            input = toBinary(sourceByte) + input;
        }
        List<Byte> results = new ArrayList<Byte>();
        while(input.length() > 0) {
            int len = input.length();
            int start = len - 7;
            if (start >= 0) {
                String sevenBits = input.substring(start, len);
                input = input.substring(0, start);
                Integer integer = Integer.valueOf(sevenBits, 2);
                byte b = (byte)(integer & 0xff);
                results.add(b);
            } else {
                break;
            }
        }
        if (results.get(results.size() - 1) == 0) {
            results.remove(results.size() - 1);
        }
        byte[] finalResult = new byte[results.size()];
        for (int i = 0; i < finalResult.length; i++) {
            finalResult[i] = results.get(i);
        }
        return finalResult;
    }

    private String toBinary(byte sourceByte) {
        return String.format("%8s", Integer.toBinaryString(sourceByte & 0xFF)).replace(' ', '0');
    }    
}

class BitString {
    byte[] bytes;
    int len;
    
    BitString(byte[] b) {
        this(b, 8 * b.length);
    }
    
    BitString(byte[] b, int len) {
        bytes = b;
        this.len = len;
    }
    
    int length() {
        return len;
    }

    int last7Bits() {
        int valStartsAtIndex = (len / 8) - 1;
        int val = 0;
        if ((valStartsAtIndex + 1) * 8 < len) {
            // 2 bytes
            val = (bytes[valStartsAtIndex] << 8) + (0xff & bytes[valStartsAtIndex + 1]);
        } else {
            // only in last byte
            val = bytes[valStartsAtIndex];
        }
        int shift = len - 8 * (len / 8);
        val = (val >> (8 - shift)) & 0x7f;
        return val;
    }

    void removeLastByte() {
        len = len - 8;
        if (len < 0) {
            len = 0;
        }
    }

    long valueOf() {
        int val = 0;
        for (int i = 0; i < len / 8; i++) {
            val = val << 8;
            val = val + bytes[i];
        }
        return val;
        /*        long val = 0;
        for (int i = 0; i * 8 < len; i++) {
            if ((i + 1) * 8 == len - 1) {
                int offset = len - (i * 8);
                int shift = 8 - offset;
                val = val << offset;
                System.err.println("val shifted up by " + offset + " to make " + val);
                int x = bytes[i];
                val = (val + x) >> shift;
                System.err.println("val totalled to " + val);
            } else {
                val = val << 8;
                System.err.println("val shifted up(2) by 8 to make " + val);
                val = val + bytes[i];
                System.err.println("val totalled(2) to " + val);
            }
        }
        return val;*/
    }
}
