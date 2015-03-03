package lytro.command;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import lytro.util.ProtocolException;

/**
 *
 */
public interface CommandInterface<ResponseType> {
    
    public ResponseType execute(Socket socket) throws IOException, ProtocolException;

}
