package lytro.command.base;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import lytro.command.BaseCommand;
import lytro.command.Packet;

/**
 *
 */
public class CameraTime extends BaseCommand<Date> {

    private final static int CMD_CODE = 0xC600;

    public CameraTime() {
        super(new Packet(CMD_CODE)
                .setParameter(0, 0x03)
                .setResponseBufferSize(14));
    }

    @Override
    protected Date processResponse(Packet response) {
        byte[] data = response.getOptionalPayload();

        int year = decodeShort(data, 0);
        int month = decodeShort(data, 2);
        int day = decodeShort(data, 4);
        int hours = decodeShort(data, 6);
        int minutes = decodeShort(data, 8);
        int seconds = decodeShort(data, 10);
        // milliseconds are always 0

        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day, hours, minutes, seconds);
        return cal.getTime();
    }

    public int decodeShort(byte[] data, int offset) {
        return ((data[offset + 1] & 0xFF) << 8) | (data[offset] & 0xFF);
    }

}
