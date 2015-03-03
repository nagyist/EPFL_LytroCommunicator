package lytro.command.advanced;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import lytro.command.CommandInterface;
import lytro.util.Constants;
import lytro.util.Log;
import lytro.util.PictureListEntry;
import lytro.util.ProtocolException;

/**
 *
 */
public class DownloadLFP implements CommandInterface<Void> {
    
    private static final String SAVE_DIRECTORY = "images";

    private final List<String> imageIds;
    private final int from;
    private final int to;
    private final File serialFile;
    private final File saveDirectory;

    public DownloadLFP(List<String> imageIds, int from, int to) {
        this.imageIds = imageIds;
        this.from = from;
        this.to = to;
        this.serialFile = new File(Constants.serialFile);
        this.saveDirectory = new File(SAVE_DIRECTORY);
    }
    
    @Override
    public Void execute(Socket socket) throws IOException, ProtocolException {
        if (!serialFile.exists()) {
            throw new ProtocolException("Unexisting serial file: " + Constants.serialFile);
        }
        if (imageIds == null || imageIds.isEmpty()) {
            throw new ProtocolException("No images id to load");
        }
        if (from < 1 || from > imageIds.size()) {
            throw new ProtocolException("Invalid start index, must be in [1, " + imageIds.size() + "]");
        }
        if (to < 1 || to > imageIds.size()) {
            throw new ProtocolException("Invalid end index, must be in [1, " + imageIds.size() + "]");
        }
        
        if (!saveDirectory.exists()) {
            Log.info("Creating image directory at: " + saveDirectory.getAbsolutePath());
            saveDirectory.mkdir();
        }
                
        List<PictureListEntry> list = new ShowPicturesList().execute(socket);
        
        for (int i = from - 1; i < to; i++) {
            String id = imageIds.get(i);
            
            PictureListEntry entry = find(list, id);
            if (entry == null) {
                Log.warning("Image not found on Lytro: " + id);
            } else {
                int index = i+1;
                Log.info("Loading file (" + index + "): " + entry.getFilePath(""));
                downloadFile(entry, socket, ".RAW", ".raw");
                downloadFile(entry, socket, ".txt", "_metadata.json");                
                copySerialFile(entry);
            }
        }
        
        return null;
    }

    private void copySerialFile(PictureListEntry entry)  {
        try {
            String fileName = entry.getDefaultName() + "_private_metadata.json";
            File serialFileCopy = new File(saveDirectory, fileName);
            Files.copy(serialFile.toPath(), serialFileCopy.toPath());
        } catch (IOException ex) {
            Log.warning("Error while copying serial file: " + ex);
        }
    }
    
    private void downloadFile(PictureListEntry entry, Socket socket, String extension, 
            String newExtension) throws ProtocolException, IOException {
        String filePath = entry.getFilePath(extension);
        File imageFile = new File(saveDirectory, entry.getDefaultName() + newExtension);
        
        new DownloadFile(filePath, imageFile).execute(socket);
//        new LoadFile(entry.getFilePath(extension)).execute(socket);
//        Integer length = new QueryContentLength().execute(socket);
//
//        if (length <= 0) {
//            throw new ProtocolException("Invalid length received after a Load File: " + length);
//        }
//                
//        byte[] data = Download.download(length, socket);        
//        
//        File imageFile = new File(saveDirectory, entry.getDefaultName() + extension);
//        Log.info("Writing image to " + imageFile);
//        try (FileOutputStream fos = new FileOutputStream(imageFile)) {
//            fos.write(data);
//        } catch (IOException ex) {
//            Log.error("Could not write image to disk:\n" + ex);
//        }
    }
    
    private PictureListEntry find(List<PictureListEntry> entries, String id) {
        for (PictureListEntry entry : entries) {
            if (entry.getPictureId().equals(id)) {
                return entry;
            }
        }
        
        return null;
    }

    
    private List<String> getImagesId() {
        List<String> result = new ArrayList<>();
        result.add("sha1-b69328a3e415faa0f2cbf2c4aae6400cd77d97d2");
        return result;
    }
}
