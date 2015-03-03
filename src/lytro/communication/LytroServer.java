package lytro.communication;

import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;
import lytro.command.CommandBuilder;
import lytro.command.CommandInterface;
import lytro.util.Log;
import lytro.util.ProtocolException;

/**
 *
 */
public class LytroServer implements Runnable {

    public static boolean exit = false;
    
    private final Scanner sc = new Scanner(System.in);

    private final String hostName;
    private final int port;

    public LytroServer(String hostName, int port) {
        this.hostName = hostName;
        this.port = port;
    }

    @Override
    public void run() {
        try (Socket socket = new Socket(hostName, port)) {
            Log.lytro("Control port connected");

            while (!exit) {
                System.out.print("User > ");
                String[] cmd = sc.nextLine().split(" ");                

                if (cmd[0].equals("exit")) {
                    exit = true;
                } else {
                    executeCommand(cmd, socket);
                }
            }
        } catch (IOException ex) {
            Log.error("An exception occured:\n" + ex);
        }
        Log.lytro("Control socket closed");
    }

    private void executeCommand(String[] cmdArray, final Socket socket) throws IOException {
        CommandInterface cmd = CommandBuilder.build(cmdArray);
        if (cmd == null) {
            Log.error("Unknown command: " + cmdArray[0]);
        } else {
            Object result;
            try {
                result = cmd.execute(socket);
                if (result != null) {
                    Log.lytro("Command result:\n" + printResult(result) + "\n");
                }
            } catch (ProtocolException ex) {
                Log.error("A protocol exception occured:\n" + ex);
            }
        }
    }
    
    // Just to print lists in pretty way
    private String printResult(Object object) {
        if (object instanceof List) {
            String result = "";
            List list = (List) object;
            for (Object element : list) {
                result += element + "\n";
            }
            
            return result;
        } else {
            return "" + object;
        }
    }
}
