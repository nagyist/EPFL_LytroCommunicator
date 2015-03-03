package lytro.util;

import java.nio.charset.Charset;

/**
 *
 */
public class Strings {
    
    public static byte[] stringToBytes(String value) {
        return value.getBytes(Charset.forName("UTF-8"));
    }
    
    public static String bytesToString(byte[] data, int offset, int length) {
        String result = "";
        
        for (int i = offset; i < offset + length; i++) {
            if (data[i] == 0) {
                return result;
            } else {
                result+= (char) data[i];
            }
        }
        
        return result;
    }

}
