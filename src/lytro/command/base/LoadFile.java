package lytro.command.base;

import java.util.Arrays;
import lytro.command.BaseCommand;
import lytro.command.Packet;
import lytro.util.Strings;

/**
 *
 */
public class LoadFile extends BaseCommand<Void> {

    private static final int CMD_CODE = 0xC200;

    public LoadFile(String path) {
        super(new Packet(CMD_CODE)
                .setParameter(0, 1)
                .setOptionalPayload(getPayLoad(path)));
    }

    @Override
    protected Void processResponse(Packet response) {
        return null;
    }
    
    private static byte[] getPayLoad(String path) {
        byte[] stringBytes = Strings.stringToBytes(path);
        
        return Arrays.copyOf(stringBytes, stringBytes.length + 1);
    }

}
