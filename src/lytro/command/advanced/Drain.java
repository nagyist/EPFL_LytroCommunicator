package lytro.command.advanced;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import lytro.command.CommandInterface;
import lytro.command.Packet;
import lytro.util.Log;

/**
 *
 */
public class Drain implements CommandInterface<Void> {

    @Override
    public Void execute(Socket socket) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        socket.setSoTimeout(5000);
        
        Log.system("Trying to read (can take up to 5 seconds)...");
        InputStream in = socket.getInputStream();

        int read;

        try {
            while ((read = in.read()) != -1) {
                baos.write(read);
            }
            Log.system("End of stream reached");
        } catch (SocketTimeoutException ex) {}
        
        if (baos.size() > 0) {
            Log.system("Drained bytes (" + baos.size() + "):");
            if (baos.size() > 512) {
                Log.system("Too many bytes to print...");
            } else {
                Log.system(Packet.printBytes(baos.toByteArray()));
            }
        } else {
            Log.system("No bytes drained");
        }
        
        socket.setSoTimeout(0);
        
        return null;
    }

}
