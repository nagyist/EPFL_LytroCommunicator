package lytro.command;

import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import lytro.command.advanced.DownloadHardwareInfo;
import lytro.command.advanced.DownloadLFP;
import lytro.command.advanced.Drain;
import lytro.command.advanced.GetPicturesIds;
import lytro.command.advanced.ShowPicturesList;
import lytro.command.base.CameraTime;
import lytro.command.base.TakePic;
import lytro.util.Constants;
import lytro.util.ProtocolException;

/**
 *
 */
public class CommandBuilder {

    public static CommandInterface build(final String[] cmd) {
        switch (cmd[0]) {
            case "takepic":
                return new TakePic();
            case "time":
                return new CameraTime();
            case "list":
                return new ShowPicturesList();
            case "drain":
                return new Drain();
            case "ids":
                return new GetPicturesIds();
            case "info":
                return new DownloadHardwareInfo();
            case "lfp":
                return new CommandInterface() {
                    @Override
                    public Void execute(Socket socket) throws IOException, ProtocolException {
                        List<String> list = new GetPicturesIds().execute(socket);

                        String path = null;
                        int from = 1;
                        int to = list.size();                        
                        
                        if (cmd.length > 1) {
                            from = toInt(cmd[1]);
                        }
                        if (cmd.length > 2) {
                            to = toInt(cmd[2]);
                        }

                        return new DownloadLFP(list, from, to).execute(socket);
                    }
                };
            case "help":
                return new CommandInterface() {
                    @Override
                    public Object execute(Socket socket) throws IOException, ProtocolException {
                        List<String> cmds = new LinkedList<>();
                        cmds.add("takepic Take a picture");
                        cmds.add("time \tShow camera time");
                        cmds.add("list \tList all pictures in camera");
                        cmds.add("ids \tShow all the ids contained in the file " + Constants.picturesIDsFileName);
                        cmds.add("lfp \tDownload all pictures whose ids are in " + Constants.picturesIDsFileName);
                        cmds.add("drain \tDrain all unread bytes from socket in (DEBUGGING TOOL)");
                        cmds.add("info \tDownload hardware info to " + Constants.infoDir);
                        return cmds;
                    }
                };
            default:
                return null;
        }
    }

    private static int toInt(String arg) throws ProtocolException {
        try {
            return Integer.parseInt(arg);
        } catch (NumberFormatException ex) {
            throw new ProtocolException("Not a number: " + arg);
        }
    }

}
