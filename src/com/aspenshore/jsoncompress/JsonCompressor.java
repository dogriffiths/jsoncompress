package com.aspenshore.jsoncompress;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonCompressor {
    private static String escapeChar = ";";
    private final static int ESCAPED_UPPERCASE = 0x01;

    public byte[] compressJson(String json) {
        int options = 0;
        String walkFormat = walkFormat(json);
        byte[] compress;
        String tickedString = walkFormat.replaceAll("([A-Z])", escapeChar + "$1");
        String upperTickedString = tickedString.toUpperCase();
        upperTickedString = Dictionary.shorten(upperTickedString);
        byte[] compressEscapedCase = compress6AndDec(upperTickedString.getBytes());
        byte[] compressUnescapedCase = compress(walkFormat.getBytes());
        if (compressEscapedCase.length < compressUnescapedCase.length) {
            compress = compressEscapedCase;
            options = options | ESCAPED_UPPERCASE;
        } else {
            compress = compressUnescapedCase;
        }
        byte[] bytesWithOptions = new byte[compress.length + 1];
        bytesWithOptions[0] = (byte)options;
        System.arraycopy(compress, 0, bytesWithOptions, 1, compress.length);
        return bytesWithOptions;
    }

    public String expandJson(byte[] bytesWithOptions) {
        int options = bytesWithOptions[0];
        byte[] bytes = new byte[bytesWithOptions.length - 1];
        System.arraycopy(bytesWithOptions, 1, bytes, 0, bytesWithOptions.length - 1);
        String expandedString;
        if ((options & ESCAPED_UPPERCASE) != 0) {
            expandedString = new String(expand6AndInc(bytes));
            expandedString = Dictionary.lengthen(expandedString);
            expandedString = expandedString.toLowerCase();
            for (char a : "abcdefghijklmnopqrstuvwxyz".toCharArray()) {
                expandedString = expandedString.replaceAll(escapeChar + a, ("" + a).toUpperCase());
            }
        } else {
            expandedString = new String(expand(bytes));
        }
        return unwalkFormat(expandedString);
    }

    void incrementEach(byte[] bytes, int inc) {
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] += inc;
        }
    }

    byte[] expand6AndInc(byte[] sourceBytes) {
        byte[] expanded = expand6(sourceBytes);
        incrementEach(expanded, 32);
        return expanded;
    }

    byte[] compress6AndDec(byte[] sourceBytes) {
        incrementEach(sourceBytes, -32);
        return compress6(sourceBytes);
    }

    byte[] expand(byte[] sourceBytes) {
        byte[] resultBytes = new byte[sourceBytes.length * 8 / 7];
        int offset = 0;
        for (int i = 0; i < resultBytes.length; i++) {
            int into = offset & 0x7;
            int byteNo = offset >> 3;
            int source = 0xff & sourceBytes[byteNo];
            if (into == 0) {
                // We're starting on a byte boundary
                resultBytes[i] = (byte)(source >> 1);
            }
            else if (into == 1) {
                // We're 1 position into a byte boundary
                resultBytes[i] = (byte)(0x7f & source);
            } else {
                // We're crossing a byte boundary
                byte firstByte = (byte)(0x7f & (source << (into - 1)));
                byte secondByte = (byte)((0xff & sourceBytes[byteNo + 1]) >> (9 - into));
                resultBytes[i] = (byte)(firstByte | secondByte);
            }
            offset += 7;
        }
        if ((resultBytes.length > 0) && (resultBytes[resultBytes.length - 1] == 0)) {
            byte[] trimmed = new byte[resultBytes.length - 1];
            System.arraycopy(resultBytes, 0, trimmed, 0, trimmed.length);
            resultBytes = trimmed;
        }
        return resultBytes;
    }

    byte[] compress(byte[] sourceBytes) {
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
            int into = offset & 0x7;
            byte importantBits = (byte)(0xff & sourceBytes[i]);
            int byteNo = offset / 8;
            if (into == 0) {
                // We're starting on a byte boundary
                resultBytes[byteNo] = (byte)(importantBits << 1);
            }
            else if (into == 1) {
                // We're 1 position into a byte boundary
                resultBytes[byteNo] |= importantBits;
            } else {
                // We're crossing a byte boundary
                resultBytes[byteNo] |= importantBits >> (into - 1);
                resultBytes[byteNo + 1] = (byte)((importantBits << (9 - into)));
            }
            offset = offset + 7;
        }
        return resultBytes;
    }

    byte[] expand6(byte[] sourceBytes) {
        byte[] resultBytes = new byte[sourceBytes.length * 8 / 6];
        int offset = 0;
        for (int i = 0; i < resultBytes.length; i++) {
            int byteNo = offset >> 3;
            int into = offset & 0x7;
            int source = 0xff & sourceBytes[byteNo];
            if (into == 0) {
                // We're starting on a byte boundary
                resultBytes[i] = (byte)(source >> 2);
            } else if (into == 2) {
                // We're 2 positions into a byte boundary
                resultBytes[i] = (byte)(0x3f & source);
            } else {
                // We're crossing a byte boundary
                byte firstByte = (byte)(0x3f & (source << (into - 2)));
                byte secondByte = (byte)(0xff & ((0xff & sourceBytes[byteNo + 1]) >> (10 - into)));
                resultBytes[i] = (byte)(0xff & (firstByte | secondByte));
            }
            offset += 6;
        }
        if ((resultBytes.length > 0) && (resultBytes[resultBytes.length - 1] == 0)) {
            byte[] trimmed = new byte[resultBytes.length - 1];
            System.arraycopy(resultBytes, 0, trimmed, 0, trimmed.length);
            resultBytes = trimmed;
        }
        return resultBytes;
    }

    byte[] compress6(byte[] sourceBytes) {
        int resultLength = sourceBytes.length * 6 / 8;
        if (resultLength * 8 < sourceBytes.length * 6) {
            resultLength++;
        }
        byte[] resultBytes = new byte[resultLength];
        int offset = 0;
        for (int i = 0; i < resultBytes.length; i++) {
            resultBytes[i] = 0;
        }
        for (int i = 0; i < sourceBytes.length; i++) {
            byte source = sourceBytes[i];
            int into = offset - ((offset / 8) * 8);
            int byteNo = offset >> 3;
            if (into == 0) {
                // We're starting on a byte boundary
                resultBytes[byteNo] = (byte)(0xff & (source << 2));
            } else if (into == 2) {
                // We're 2 positions into a byte boundary
                resultBytes[byteNo] |= source;
            } else {
                // We're crossing a byte boundary
                resultBytes[byteNo] |= source >> (into - 2);
                resultBytes[byteNo + 1] = (byte)(0xff & (source << (10 - into)));
            }
            offset += 6;
        }
        return resultBytes;
    }
    
    String walkFormat(String json) {
        String s = json.trim();
        StringBuilder sb = new StringBuilder();
        char[] chars = s.toCharArray();
        boolean inString = false;
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (c == '"') {
                inString = !inString;
                continue;
            }
            if ((c == ' ') && !inString) {
                continue;
            }
            if ((c == '\n') && !inString) {
                continue;
            }
            if ((c == '}') && !inString) {
                sb.append('^');
                continue;
            }
            if ((c == ']') && !inString) {
                sb.append('^');
                continue;
            }
            if ((c == '{') && !inString) {
                sb.append('+');
                continue;
            }
            if ((c == '[') && !inString) {
                sb.append('*');
                continue;
            }
            if ((c == ',') && !inString) {
                sb.append('>');
                continue;
            }
            if ((c == ':') && !inString) {
                sb.append('>');
                continue;
            }
            sb.append(c);
        }
        String s1 = sb.toString();
        if (s1.startsWith("+")) {
            s1 = s1.substring(1, s1.length());
        }
        while (s1.endsWith("^")) {
            s1 = s1.substring(0, s1.length() - 1);
        }
        //        s1=s1.replaceAll("\\>\\+", "+");
        //s1=s1.replaceAll("\\>\\*", "*");
        //s1=s1.replaceAll("\\^\\>", "^");
        //s1=s1.replaceAll("\\^\\+", "^");
        return s1;
    }

    String aWalk;
    int pos = 0;
    String unwalkFormat(String walk) {
        aWalk = walk;
        if (!aWalk.startsWith("*")) {
            aWalk = "+" + aWalk + "^";
        }
        pos = 0;
        Object jsonObject = null;
        try {
            jsonObject = readWalk();
        } catch (JSONException e) {
            throw new RuntimeException("Can't parse walk", e);
        }
        return jsonObject.toString();
    }

    Object readWalk() throws JSONException {
        char c = aWalk.charAt(pos);
        if (c == '+') {
            pos++;
            return readMap();
        }
        if (c == '*') {
            pos++;
            return readArray();
        }
        return readString();
    }

    JSONObject readMap() throws JSONException {
        char c = aWalk.charAt(pos);
        JSONObject result = new JSONObject();
        while ((pos < aWalk.length()) && (c != '^')) {
            String key = readString();
            c = aWalk.charAt(++pos);
            if (c == '>') {
                pos++;
            }
            Object value = readWalk();
            result.put(key, value);
            if (pos == aWalk.length() - 1) {
                break;
            }
            c = aWalk.charAt(++pos);
            if (c == '>') {
                pos++;
            }
        }
        return result;
    }

    JSONArray readArray() throws JSONException {
        char c = aWalk.charAt(pos);
        JSONArray result = new JSONArray();
        while ((pos < aWalk.length()) && (c != '^')) {
            Object value = readWalk();
            result.put(value);
            if (pos == aWalk.length() - 1) {
                break;
            }
            c = aWalk.charAt(++pos);
            if (c == '>') {
                pos++;
            }
        }
        return result;
    }

    String readString() {
        StringBuilder sb = new StringBuilder();
        char c = aWalk.charAt(pos);
        int start = pos;
        while ((c != '^') && (c != '>') && (c != '+') && (c != '*')) {
            sb.append(c);
            if (pos == aWalk.length() - 1) {
                pos++;
                break;
            }
            c = aWalk.charAt(++pos);
        }
        pos--;
        return sb.toString();
    }
}
