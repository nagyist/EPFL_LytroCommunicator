package lytro.command.base;

import java.io.IOException;
import lytro.command.BaseCommand;
import lytro.command.Packet;
import static lytro.util.Endian.readLittleEndian;
import static lytro.util.Endian.readLittleEndian;
import lytro.util.Log;
import lytro.util.ProtocolException;

/**
 *
 */
public class QueryContentLength extends BaseCommand<Integer> {

    private static final int CMD_MODE = 0xC600;

    public QueryContentLength() {
        super(new Packet(CMD_MODE)
                .setResponseBufferSize(4));
    }

    @Override
    protected Integer processResponse(Packet response) throws ProtocolException {
        byte[] data = response.getOptionalPayload();
        if (data.length != 4) {
            throw new ProtocolException("(QCL) Invalid payload received: "
                    + "expected 4 bytes but got " + data.length);
        }

        return readLittleEndian(data, 0);
    }

}
