package lytro.command.advanced;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import lytro.command.CommandInterface;
import lytro.command.Packet;
import lytro.command.base.Download;
import lytro.command.base.LoadPictureList;
import lytro.command.base.QueryContentLength;
import lytro.util.Log;
import static lytro.util.Log.info;
import lytro.util.PictureListEntry;
import lytro.util.ProtocolException;

/**
 *
 */
public class ShowPicturesList implements CommandInterface<List<PictureListEntry>> {

    @Override
    public List<PictureListEntry> execute(Socket socket) throws IOException, ProtocolException {
        info("Loading pictures list");
        LoadPictureList loadPictureList = new LoadPictureList();
        loadPictureList.execute(socket);

        QueryContentLength queryContentLength = new QueryContentLength();
        Integer length = queryContentLength.execute(socket);

        if (length <= 0) {
            throw new ProtocolException("Invalid length received after a Load List: " + length);
        }
        
        byte[] data = Download.download(length, socket);
                
        return decodeList(data);
    }

    
    private List<PictureListEntry> decodeList(byte[] data) throws ProtocolException {
        int offset = 0x5C;
        if (offset >= data.length) {
            throw new ProtocolException("Missing data, no list found");
        }
        
        List<PictureListEntry> result = new LinkedList<>();
        while (offset + 127 < data.length) {
            result.add(PictureListEntry.loadFromBytes(data, offset));
            offset += 128;
        }
        
        if (offset != data.length) {
            int leftBytes = data.length - offset;
            Log.warning(leftBytes + " bytes where ignored");
        }
        
        return result;
    }

}
