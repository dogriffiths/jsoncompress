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
        bytes = b;
        len = 8 * bytes.length;
    }
    
    void append(BitString s) {
    }

    int length() {
        return len;
    }

    BitString last7Bits() {
        return null;
    }

    BitString allExceptLastByte() {
        return null;
    }

    int valueOf() {
        int val = 0;
        for (int i = 0; i * 8 < len; i++) {
            System.err.println("i = " + i);
            if (i * 8 == len - 1) {
                int offset = len - (i * 8);
                int shift = 8 - offset;
                val = val << offset;
                int x = bytes[i] >> shift;
                val = val + x;
            } else {
                val = val << 8;
                val = val + bytes[i];
            }
        }
        return val;
    }
}
