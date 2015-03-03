package lytro.command.base;

import java.io.IOException;
import lytro.command.BaseCommand;
import lytro.command.Packet;

/**
 *
 */
public class LoadHardwareInfo extends BaseCommand<Void> {
    
    private static final int CMD_CODE = 0xC200;

    public LoadHardwareInfo() {
        super(new Packet(CMD_CODE));
    }

    @Override
    protected Void processResponse(Packet response) {        
        return null;
    }
    
    
    private void getCameraManufacturer(byte[] data) {
        int startIndex = 0;
        while (startIndex < 255 && data[startIndex] == 0) {
            startIndex++;
        }
        
        System.out.println(startIndex);
        for (int i = startIndex; i < 255-28; i++) {
            System.out.print(String.format("%02X", data[i]));
            if (i % 4 == 0) {
                System.out.println();
            }
        }
    }
}
