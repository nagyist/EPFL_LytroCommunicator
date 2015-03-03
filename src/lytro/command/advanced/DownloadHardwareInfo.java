package lytro.command.advanced;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import lytro.command.CommandInterface;
import lytro.util.Constants;
import lytro.util.Log;
import lytro.util.ProtocolException;

/**
 *
 */
public class DownloadHardwareInfo implements CommandInterface<Void> {

    @Override
    public Void execute(Socket socket) throws IOException, ProtocolException {
        File infoDir = new File(Constants.infoDir);
        if (!infoDir.exists()) {
            Log.info("Creating info dir: " + Constants.infoDir);
            infoDir.mkdir();
        }
        
        new DownloadFile("B:\\LENSODOMETER.TXT", 
                new File(Constants.infoDir, "lensodometer.txt")).execute(socket);
        new DownloadFile("B:\\USER.TXT", 
                new File(Constants.infoDir, "user.txt")).execute(socket);
        new DownloadFile("B:\\STATE.TXT", 
                new File(Constants.infoDir, "state.txt")).execute(socket);
        new DownloadFile("B:\\SETTINGS.TXT", 
                new File(Constants.infoDir, "settings.txt")).execute(socket);
        new DownloadFile("A:\\FIRMWARE.TXT", 
                new File(Constants.infoDir, "firmware.txt")).execute(socket);
        
        return null;
    }

}
