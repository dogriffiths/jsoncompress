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
                byte firstByte = (byte)(0x7f & ((0xff & sourceBytes[firstByteNo]) << (into - 1)));
                byte secondByte = (byte)(0xff & ((0xff & sourceBytes[secondByteNo]) >> (9 - into)));
                resultBytes[i] = (byte)(0xff & (firstByte | secondByte));
            }
            offset = offset + 7;
        }
        if ((resultBytes.length > 0) && (resultBytes[resultBytes.length - 1] == 0)) {
            byte[] trimmed = new byte[resultBytes.length - 1];
            System.arraycopy(resultBytes, 0, trimmed, 0, trimmed.length);
            resultBytes = trimmed;
        }
        return resultBytes;
    }

    public byte[] compress(byte[] sourceBytes) {
        int resultLength = sourceBytes.length * 7 / 8;
        if (resultLength * 8 < sourceBytes.length * 7) {
            resultLength++;
        }
        byte[] resultBytes = new byte[resultLength];
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
                resultBytes[byteNo + 1] = (byte)(0xff & ((importantBits << (9 - into))));
            }
            offset = offset + 7;
        }
        return resultBytes;
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
            int shift = len - 8 * (len / 8);
            val = (val >> (8 - shift));
        } else {
            // only in last byte
            val = bytes[valStartsAtIndex];
            if ((len / 8) * 8 != len) {
                int shift = len - 8 * (len / 8);
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
