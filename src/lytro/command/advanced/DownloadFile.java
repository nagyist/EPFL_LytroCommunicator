package lytro.command.advanced;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import lytro.command.CommandInterface;
import lytro.command.base.Download;
import lytro.command.base.LoadFile;
import lytro.command.base.QueryContentLength;
import lytro.util.Log;
import lytro.util.PictureListEntry;
import lytro.util.ProtocolException;

/**
 *
 */
public class DownloadFile implements CommandInterface<Void> {

    private final String path;
    private final File outputFile;

    public DownloadFile(String path, File outputFile) {
        this.path = path;
        this.outputFile = outputFile;
    }
    
    @Override
    public Void execute(Socket socket) throws IOException, ProtocolException {
        new LoadFile(path).execute(socket);
        Integer length = new QueryContentLength().execute(socket);

        if (length <= 0) {
            throw new ProtocolException("Invalid length received after a "
                    + "Load File (" + path + "): " + length);
        }
        
        byte[] data = Download.download(length, socket);        
        
        Log.info("Writing file to " + outputFile);
        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            fos.write(data);
        } catch (IOException ex) {
            Log.error("Could not write file to disk:\n" + ex);
        }
        
        return null;
    }
    

}
