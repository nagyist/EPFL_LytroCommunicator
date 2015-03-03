package lytro.command.advanced;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import lytro.command.CommandInterface;
import lytro.util.Constants;
import lytro.util.Log;
import lytro.util.ProtocolException;

public class GetPicturesIds implements CommandInterface<List<String>> {
    
    @Override
    public List<String> execute(Socket socket) throws IOException, ProtocolException {
        List<String> picturesIDs = new LinkedList<>();

        Log.info("Loading images id at " + Constants.picturesIDsFileName + "...");

        try (BufferedReader br = new BufferedReader(new FileReader(Constants.picturesIDsFileName))) {
            String line;

            while ((line = br.readLine()) != null) {
                picturesIDs.add(line);
            }

        } catch (IOException ex) {
            throw new ProtocolException(ex);
        }

        return picturesIDs;
    }

}
