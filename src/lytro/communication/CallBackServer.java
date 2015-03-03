package lytro.communication;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import lytro.util.Constants;
import static lytro.util.Constants.*;
import lytro.util.Log;

/**
 *
 */
public class CallBackServer implements Runnable {

    private static final String SPACE = " ";

    private final int port;
    private final String hostName;
    private final File picturesIDFile = new File(Constants.picturesIDsFileName);

    public CallBackServer(int port, String hostName) {
        this.port = port;
        this.hostName = hostName;

    }

    @Override
    public void run() {
        try (BufferedReader reader = getReader()) {
            Log.lytro("Callback port connected");
            String line;
            while (!LytroServer.exit && (line = reader.readLine()) != null) {
                message(line);
            }            

        } catch (IOException ex) {
            Log.error("An exception occured: " + ex);
        }
        Log.lytro("Callback socket closed");
    }

    private void message(String message) {

        String callbackName = getCallbackName(message);

        if (!callbackName.equals(CB_HEARTBEAT_TICK)) {
            Log.lytro(message);
        }

        switch (callbackName) {
            case CB_HEARTBEAT_TICK:
                break;

            case CB_NEW_PICTURE_AVAILABLE:

                try (BufferedWriter writer
                        = new BufferedWriter(new FileWriter(picturesIDFile, true))) {
                    writer.write(getCallbackMessage(message) + "\n");
                    writer.flush();
                } catch (IOException e) {
                    Log.error("Error while writing image id: " + e);
                }
                break;

            default:
                break;
        }

    }

    private String getCallbackName(String message) {
        return message.split(SPACE)[0].trim();
    }

    private String getCallbackMessage(String message) {
        String callbackName = getCallbackName(message);
        return message.substring(callbackName.length() + 1).trim();
    }

    private BufferedReader getReader() throws IOException {
        Socket socket = new Socket(hostName, port);
        return new BufferedReader(new InputStreamReader(socket.getInputStream())) {
            // Hack to remove all trailing bytes that are = 0
            @Override
            public String readLine() throws IOException {
                String line = super.readLine();
                if (line == null) {
                    return null;
                }
                
                String result = "";

                char[] ch = line.toCharArray();
                for (int i = ch.length-1; i >= 0; i--) {
                    if (ch[i] == 0) {
                        return result;
                    } else {
                        result = (char) ch[i] + result;
                    }
                }

                return result;
            }
        };
    }

}
