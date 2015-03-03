package lytro.command;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;
import lytro.util.Log;
import lytro.util.ProtocolException;

/**
 *
 */
public abstract class BaseCommand<ResponseType> implements CommandInterface<ResponseType> {

    private final Packet packet;

    public BaseCommand(Packet packet) {
        this.packet = packet;
    }

    @Override
    public ResponseType execute(Socket socket) throws IOException, ProtocolException {
        if (Log.LOG_PACKETS) {
            Log.system("Packet sent:");
            Log.system(this.toString());
        }
        
        OutputStream out = socket.getOutputStream();
        out.write(packet.getBytes());
        out.flush();
        
        // Actually, ALL base commands receive a response
        Packet response = new Packet(socket.getInputStream());
        
        if (Log.LOG_PACKETS) {            
            Log.system("Packet Received:");
            Log.system(response.toString());
        }
        
        testResponseIntegrity(response);
        return processResponse(response);
    }

    protected abstract ResponseType processResponse(Packet response) throws ProtocolException;
    
    protected void testResponseIntegrity(Packet response) throws ProtocolException {
        if (!response.isResponse()) {
            throw new ProtocolException("Response is not marked as one");
        }
        if (response.getCommand() != packet.getCommand()) {
            throw new ProtocolException("Response has not the same command code");
        }
        if (!Arrays.equals(response.getParameters(), packet.getParameters())) {
            Log.warning("Response has not the same parameters");
        }
    }


    @Override
    public String toString() {
        return packet.toString();
    }

}
