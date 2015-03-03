package lytro.command.base;

import lytro.command.BaseCommand;
import lytro.command.Packet;

/**
 *
 */
public class TakePic extends BaseCommand<Void> {
    
    private static final int CMD_MODE = 0xC000;

    public TakePic() {
        super(new Packet(CMD_MODE));
    }

    @Override
    protected Void processResponse(Packet response) {
        return null;
    }
}
