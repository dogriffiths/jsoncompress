import java.util.ArrayList;
import java.util.List;

public class JsonCompressor {

    public byte[] expand(byte[] sourceBytes) {
        byte[] resultBytes = new byte[sourceBytes.length * 8 / 7];
        int offset = 0;
        for (int i = 0; i < resultBytes.length; i++) {
            if ((offset / 8) * 8 == offset) {
                // We're starting on a byte boundary
                int byteNo = offset / 8;
                resultBytes[i] = (byte)(0xff & ((0xff & sourceBytes[byteNo]) >> 1));
            }
            else if (((offset - 1) / 8) * 8 == offset - 1) {
                // We're 1 position into a byte boundary
                int byteNo = offset / 8;
                resultBytes[i] = (byte)(0xff & ((0x7f & sourceBytes[byteNo])));
            } else {
                // We're crossing a byte boundary
                int firstByteNo = offset / 8;
                int secondByteNo = firstByteNo + 1;
                int into = offset - ((offset / 8) * 8);
                byte firstByte = (byte)(0xff & ((0xff & sourceBytes[firstByteNo]) >> into));
                byte secondByte = (byte)(0xff & ((0xff & sourceBytes[secondByteNo]) >> (8 - into)));
                resultBytes[i] = (byte)(0xff & ((firstByte << (into - 1)) | secondByte));
            }
            offset = offset + 7;
        }
        return resultBytes;
    }

    public byte[] compress(byte[] sourceBytes) {
        byte[] resultBytes = new byte[sourceBytes.length * 7 / 8];
        int offset = 0;
        for (int i = 0; i < resultBytes.length; i++) {
            resultBytes[i] = 0;
        }
        for (int i = 0; i < sourceBytes.length; i++) {
            byte importantBits = (byte)(0xff & sourceBytes[i]);
            if ((offset / 8) * 8 == offset) {
                // We're starting on a byte boundary
                int byteNo = offset / 8;
                resultBytes[byteNo] = (byte)(0xff & (importantBits << 1));
            }
            else if (((offset - 1) / 8) * 8 == offset - 1) {
                // We're 1 position into a byte boundary
                int byteNo = offset / 8;
                resultBytes[byteNo] = (byte)(0xff & (resultBytes[byteNo] | importantBits));
            } else {
                // We're crossing a byte boundary
                int byteNo = offset / 8;
                int into = offset - ((offset / 8) * 8);
                resultBytes[byteNo] = (byte)(0xff & (resultBytes[byteNo] | (importantBits >> (into - 1))));
                resultBytes[byteNo + 1] = (byte)(0xff & ((importantBits << (8 - into))));
            }
            offset = offset + 7;
        }
        return resultBytes;
    }

    public byte[] compress2(byte[] sourceBytes) {
        String input = "";
        for (byte sourceByte : sourceBytes) {
            input = toBinary(sourceByte).substring(1,8) + input;
        }
        List<Byte> results = new ArrayList<Byte>();
        while(input.length() > 0) {
            int len = input.length();
            //            System.err.println("input.length() = " + input.length());
            int start = len - 8;
            if (start < 0) {
                start = 0;
            }
            String sevenBits = input.substring(start, len);
            input = input.substring(0, start);
            //System.err.println("now input.length() = " + input.length());
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

    public byte[] expand2(byte[] sourceBytes) {
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
            //            System.err.println("2 bytes");
            val = (bytes[valStartsAtIndex] << 8) + (0xff & bytes[valStartsAtIndex + 1]);
            //System.err.println("val = " + val);
            int shift = len - 8 * (len / 8);
            //System.err.println("shift = " + shift);
            val = (val >> (8 - shift));
            //System.err.println("val now = " + val);
        } else {
            // only in last byte
            //System.err.println("1 byte");
            //System.err.println("valStartsAtIndex = " + valStartsAtIndex);
            val = bytes[valStartsAtIndex];
            //System.err.println("val = " + val);
            if ((len / 8) * 8 != len) {
                int shift = len - 8 * (len / 8);
                //System.err.println("shift = " + shift);
                val = (val >> (8 - shift));
            }
        }
        return val & (0x7f);
    }

    void removeLastByte() {
        len = len - 8;
        if (len < 0) {
            len = 0;
        }
    }

    void removeLast7Bits() {
        len = len - 7;
        if (len < 0) {
            len = 0;
        }
    }

    long valueOf() {
        int val = 0;
        int lastComplete = (len / 8);
        for (int i = 0; i < lastComplete; i++) {
            val = val << 8;
            val = val + bytes[i];
        }
        if (lastComplete * 8 != len) {
            int diff = len - ((len / 8) * 8);
            val = val << diff;
            int extra = ((0xff & bytes[lastComplete]) >> (8 - diff));
            val = val + extra;
        }
        return val;
    }

    public String toString() {
        String result = "";
        int lastComplete = (len / 8);
        for (int i = 0; i < lastComplete; i++) {
            result = result + "," + (0xff & bytes[i]);
        }
        if (lastComplete * 8 != len) {
            int diff = len - ((len / 8) * 8);
            int extra = ((0xff & bytes[lastComplete]) >> (8 - diff));
            result = result + "," + (0xff & extra);
        }
        return result;
    }
}
