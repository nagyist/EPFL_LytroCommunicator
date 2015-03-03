package lytro.util;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 */
public class Endian {

    public static void writeLittleEndian(ByteArrayOutputStream out, int v) {
        out.write((v >>> 0) & 0xFF);
        out.write((v >>> 8) & 0xFF);
        out.write((v >>> 16) & 0xFF);
        out.write((v >>> 24) & 0xFF);
    }

    public static void writeBigEndian(ByteArrayOutputStream out, int v) {
        out.write((v >>> 24) & 0xFF);
        out.write((v >>> 16) & 0xFF);
        out.write((v >>> 8) & 0xFF);
        out.write((v >>> 0) & 0xFF);
    }

    public static int readBigEndian(InputStream in) throws IOException {
        int ch1 = in.read();
        int ch2 = in.read();
        int ch3 = in.read();
        int ch4 = in.read();
        if ((ch1 | ch2 | ch3 | ch4) < 0) {
            throw new EOFException();
        }
        return (((ch1 & 0xFF) << 24) 
                + ((ch2 & 0xFF) << 16) 
                + ((ch3 & 0xFF) << 8) 
                + ((ch4 & 0xFF) << 0));
    }

    public static int readLittleEndian(InputStream in) throws IOException {
        int ch1 = in.read();
        int ch2 = in.read();
        int ch3 = in.read();
        int ch4 = in.read();
        if ((ch1 | ch2 | ch3 | ch4) < 0) {
            throw new EOFException();
        }
        return (((ch4 & 0xFF) << 24) 
                + ((ch3 & 0xFF) << 16) 
                + ((ch2 & 0xFF) << 8) 
                + ((ch1 & 0xFF) << 0));
    }
    
    public static int readLittleEndian(byte[] data, int offset) {
        if (offset + 3 >= data.length) {
            throw new IllegalArgumentException("Must have 4 available bytes");
        }
        
        return (((data[offset+3] & 0xFF) << 24) 
                + ((data[offset+2] & 0xFF) << 16) 
                + ((data[offset+1] & 0xFF) << 8) 
                + ((data[offset] & 0xFF) << 0));
    }
    
}
