package lytro.command.base;

import lytro.command.BaseCommand;
import lytro.command.Packet;

/**
 *
 */
public class LoadPictureList extends BaseCommand<Void> {
    
    private static final int CMD_MODE = 0xC200;

    public LoadPictureList() {
        super(new Packet(CMD_MODE).setParameter(0, 0x02));
    }

    @Override
    protected Void processResponse(Packet response) {        
        return null;
    }

    
}
