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

    private String toBinary(byte sourceByte) {
        return String.format("%8s", Integer.toBinaryString(sourceByte & 0xFF)).replace(' ', '0');
    }    
}
