package lytro.command.base;

import java.io.IOException;
import java.net.Socket;
import lytro.command.BaseCommand;
import lytro.command.Packet;
import lytro.util.Log;
import lytro.util.ProtocolException;

/**
 * 
 */
public class Download extends BaseCommand<byte[]> {
    
    private static final int DEFAULT_BUFFER_SIZE = 2 << 20; // 2 MBprivate static final int DEFAULT_BUFFER_SIZE = 2 << 20; // 2 MB
    
    public static byte[] download(int totalLength, Socket socket) throws IOException, ProtocolException {
        return download(socket, totalLength, DEFAULT_BUFFER_SIZE);
    }
    
    private static byte[] download(Socket socket, int totalLength, int bufferSize) throws IOException, ProtocolException {
        byte[] resultData = new byte[totalLength];
        
        int partsCount = (int) Math.ceil((double) totalLength / (double) bufferSize);
        
        Log.downloadAnnounce(totalLength, partsCount);
        
        for (int i = 0; i < partsCount; i++) {
            int offset = i * bufferSize;
            int expectedLength = Math.min(bufferSize, totalLength - offset);
            
            byte[] part = new Download(0, offset, bufferSize).execute(socket);
            Log.downloadProgress();
            
            if (part.length != expectedLength) {
                Log.downloadEnd(true);
                throw new ProtocolException("Download part " + (i+1) + " is invalid\n"
                        + "Expected size of " + expectedLength + " but got " + part.length);
            }
            
            System.arraycopy(part, 0, resultData, offset, expectedLength);
        }
        
        Log.downloadEnd(false);
        
        return resultData;
    }
    
        
    private static final int CMD_CODE = 0xC400;    
    
    /**
     * Construct with default buffer size (2 MB)
     * @param firstByte
     * @param offset 
     */
    public Download(int firstByte, int offset) {
        this(firstByte, offset, DEFAULT_BUFFER_SIZE);
    }
    public Download(int firstByte, int offset, int bufferSize) {
        super(new Packet(CMD_CODE)
                .setParameter(0, firstByte & 0xFF)
                .setIntegerParameter(1, offset)
                .setResponseBufferSize(bufferSize)
        );
    }

    @Override
    protected byte[] processResponse(Packet response) {
        return response.getOptionalPayload();
    }
    
}
