package lytro.util;

import java.io.IOException;
import static lytro.util.Endian.readLittleEndian;
import static lytro.util.Endian.readLittleEndian;
import static lytro.util.Strings.bytesToString;

/**
 *
 */
public class PictureListEntry {
    
    public static PictureListEntry loadFromBytes(byte[] data, int startingOffset) {
        if (startingOffset + 127 >= data.length) {
            throw new IllegalArgumentException("Not enough bytes");
        }
        
        int offset = startingOffset;
        
        String folderName = bytesToString(data, offset, 8);
        offset+= 8;
        
        String fileNamePrefix = bytesToString(data, offset, 8);
        offset+= 8;
        
        int folderNumber = readLittleEndian(data, offset);
        offset+= 4;
        
        int fileNumber = readLittleEndian(data, offset);
        
        
        offset = startingOffset + 0x28;
        boolean liked = readLittleEndian(data, offset) == 1;
        offset+= 4;
        
        float lambda = readLittleEndian(data, offset);
        offset+= 4;
        
        String pictureId = bytesToString(data, offset, 48);
        offset+= 48;
        
        String date = bytesToString(data, offset, 24);
        
        offset = startingOffset + 0x7C;
        int rotation = readLittleEndian(data, offset);
        switch (rotation) {
            case 6:
                rotation = 270;
                break;
            case 3:
                rotation = 180;
                break;
            case 8:
                rotation = 90;
                break;
            case 0:
            case 1:
                rotation = 0;
                break;
            default:
                Log.warning("Unknown rotation code: " + rotation);
                rotation = 0;
        }
        
        return new PictureListEntry(folderName, fileNamePrefix, folderNumber, 
                fileNumber, liked, lambda, pictureId, date, rotation);
    }
    
    
    private final String folderName;
    private final String fileNamePrefix;
    
    private final int folderNumber;
    private final int fileNumber;
    private final boolean liked;
    
    private final float lastLambda;
    private final String pictureId;
    private final String date;
    private final int rotationAngle;

    public PictureListEntry(String folderName, String fileNamePrefix, int folderNumber, int fileNumber, boolean liked, float lastLambda, String pictureId, String date, int rotationAngle) {
        this.folderName = folderName;
        this.fileNamePrefix = fileNamePrefix;
        this.folderNumber = folderNumber;
        this.fileNumber = fileNumber;
        this.liked = liked;
        this.lastLambda = lastLambda;
        this.pictureId = pictureId;
        this.date = date;
        this.rotationAngle = rotationAngle;
    }

    public String getFilePath(String extension) {
        String number = String.format("%04d", fileNumber);
        return "I:\\DCIM\\" + folderNumber + folderName + "\\" + fileNamePrefix + number + extension;
    }
    
    public String getDefaultName() {
        String number = String.format("%04d", fileNumber);
        return fileNamePrefix + number + "__frame";
    }

    public String getPictureId() {
        return pictureId;
    }
    
    @Override
    public String toString() {
        String likeCaption = liked ? "*" : "";
        String number = String.format("%04d", fileNumber);
        return likeCaption + pictureId + 
                ": " + folderNumber + folderName + "\\" + fileNamePrefix + number
                + ", l=" + lastLambda + ",\tr=" + rotationAngle + "°\t" + date; 
    }
    
}
