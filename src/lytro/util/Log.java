package lytro.util;

import java.text.DecimalFormat;

/**
 *
 */
public class Log {
    public static final boolean LOG_PACKETS = false;
    public static final boolean LOG_INFO = true;
    
    public static void lytro(String message) {
        System.out.println("Lytro > " + message);
    }

    public static void info(String message) {
        if (LOG_INFO) {
            System.out.println("[INFO] " + message);
        }
    }
    
    public static void system(String message) {
        System.out.println(message);
    }
    
    public static void warning(String message) {
        System.out.println("[WARNING] " + message);
    }
    
    public static void error(String message) {
        System.out.println("[ERROR] " + message);
    }
    
    public static void downloadAnnounce(int fileSize, int parts) {
        System.out.print("Downloading " + parts + " part(s) (" + readableFileSize(fileSize) + ")... [");
    }
    
    public static void downloadProgress() {
        System.out.print("-");
    }
    
    public static void downloadEnd(boolean error) {
        if (error) {
            System.out.println("/!\\");
        } else {
            System.out.println("]");
        }
    }
    
    private static String readableFileSize(long size) {
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }
}
