package lytro.command;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import static lytro.util.Endian.readBigEndian;
import static lytro.util.Endian.readLittleEndian;
import static lytro.util.Endian.writeBigEndian;
import static lytro.util.Endian.writeLittleEndian;



/**
 *
 */
public class Packet {
    
    private static final int BYTES_WIDTH = 16;
    
    public static String printBytes(byte[] bytes) {
        String result = "";
        int byteIndex = 0;
        int i = 0; 
        for (byte b : bytes) {
            if (i == 0) {
                i = BYTES_WIDTH;
                result += byteIndex + "\t";
            }
            i--;

            byteIndex++;
            result += String.format("%02X ", b);

            if (i == 0) {
                result += "\n";
            }
        }

        return result;
    }

    private static final int PARAMETERS_LENGTH = 14;
    public static final int MAGIC_NUMBER = 0xAF55AAFA;

    private int length = 0;
    private boolean hasNoPayload = true;
    private boolean isResponse = false;
    private final int command;
    private byte[] parameters = new byte[PARAMETERS_LENGTH];
    private byte[] optionalPayload = new byte[0];

    public Packet(InputStream socketIn) throws IOException {
        DataInputStream in = new DataInputStream(socketIn);
        
        
        int magicNumber = readBigEndian(in);
        if (magicNumber != MAGIC_NUMBER) {
            System.out.println("[WARNING] Magic number is not correct:" + String.format("%08X", magicNumber));
        }

        length = readLittleEndian(in);
        int flags = readLittleEndian(in);

        isResponse = (flags & 0b10) == 0b10;
        hasNoPayload = (flags & 0b01) == 0b01;

        int cmd1 = in.read();
        int cmd2 = in.read();
        command = ((cmd1 & 0xFF) << 8) | (cmd2 & 0xFF);

        in.readFully(parameters);

        /*
         /!\ HACK /!\
         This constructor is used for building responses, now,
         responses always come with the flag ... 11, saying it's a
         response AND it has no payload in the request.
         However, it may have a payload and 'length' always points
         to the payload size (since it would not make any sense to
         interpret 'length' as anything else).
        
         Therefore, to know if there is a payload or not, instead of 
         looking at the flag 'hasNoPayload', we look at 'length'
         */
        if (length > 0) {
            // Note: Normally, the payload should NOT be read if the flag ends with 1
            // However, testing showed that responses have a flag ending with 1 even
            // if it contains payload

            // If has no payload, then length is payload length
            optionalPayload = new byte[length];
            in.readFully(optionalPayload);
        }

    }

    public Packet(int command) {
        this.command = command & 0xFFFF;
    }

    public Packet setIntegerParameter(int offset, int integerValue) {
        // Uses little endian
        setParameter(offset, (integerValue >>> 0) & 0xFF);
        setParameter(offset + 1, (integerValue >>> 8) & 0xFF);
        setParameter(offset + 2, (integerValue >>> 16) & 0xFF);
        setParameter(offset + 3, (integerValue >>> 24) & 0xFF);
        return this;
    }

    public Packet setParameter(int offset, int byteValue) {
        if (offset < 0 || offset >= PARAMETERS_LENGTH) {
            throw new IllegalArgumentException("Invalid offset: " + offset);
        }

        this.parameters[offset] = (byte) (byteValue & 0xFF);
        return this;
    }

    public Packet setParameters(byte[] parameters) {
        if (parameters.length != PARAMETERS_LENGTH) {
            throw new IllegalArgumentException("Wrong size");
        }
        this.parameters = parameters;

        return this;
    }

    public Packet setOptionalPayload(byte[] payload) {
        if (length != 0) {
            throw new IllegalArgumentException("Cannot set a payload AND a response buffer length");
        }

        optionalPayload = payload;
        hasNoPayload = false;
        length = payload.length;

        return this;
    }

    public Packet setResponseType() {
        isResponse = true;

        return this;
    }

    public Packet setResponseBufferSize(int size) {
        if (!hasNoPayload) {
            throw new IllegalArgumentException("Cannot set a payload AND a response buffer length");
        }
        length = size;
        return this;
    }

    public int getCommand() {
        return command;
    }

    public byte[] getParameters() {
        return parameters;
    }

    public byte[] getOptionalPayload() {
        return optionalPayload;
    }

    public boolean isResponse() {
        return isResponse;
    }

    public int getLength() {
        return length;
    }

    @Override
    public String toString() {
        int limit = 128;
        if (optionalPayload.length > limit) {
            return printBytes(getBytes(limit)) + "\n... (" + optionalPayload.length + ")";
        } else {
            return printBytes(getBytes());
        }
    }

    public byte[] getBytes() {
        return getBytes(Integer.MAX_VALUE);
    }
    public byte[] getBytes(int limit) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(28 + optionalPayload.length);

        writeBigEndian(baos, MAGIC_NUMBER);
        writeLittleEndian(baos, length);
        writeLittleEndian(baos, getFlags());

        baos.write(command >>> 8 & 0xFF);
        baos.write(command >>> 0 & 0xFF);

        baos.write(parameters, 0, 14);

        if (optionalPayload.length > 0) {
            baos.write(optionalPayload, 0, Math.min(limit, optionalPayload.length));
        }

        return baos.toByteArray();
    }

    private int getFlags() {
        int flag = 0;
        if (hasNoPayload) {
            flag = 1;
        }
        if (isResponse) {
            flag = flag | 0b10;
        }

        return flag;
    }

    
}
