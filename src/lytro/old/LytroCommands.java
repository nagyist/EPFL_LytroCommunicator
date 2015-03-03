package lytro.old;

public class LytroCommands {
	
	private static final String BYTE_NULL = "00";
	private static final String TWO_BYTES_NULL = BYTE_NULL + BYTE_NULL;
	private static final String FOUR_BYTES_NULL = TWO_BYTES_NULL + TWO_BYTES_NULL;
	private static final String PARAMETER_NULL = FOUR_BYTES_NULL + FOUR_BYTES_NULL + FOUR_BYTES_NULL + BYTE_NULL;
	
	
	private static final String MAGIC_NUMBER = "AF 55 AA FA";
	
	private static final String CMD_LOAD_HARDWARE_INFO 				= "C2 00 00";
	private static final String CMD_LOAD_FILE 						= "C2 00 01";
	private static final String CMD_LOAD_PICTURE_LIST 				= "C2 00 02";
	private static final String CMD_LOAD_PICTURE 					= "C2 00 05";
	private static final String CMD_LOAD_CALIBRATION_DATA 			= "C2 00 06";
	private static final String CMD_LOAD_COMPRESSED_RAW_PICTURE		= "C2 00 07";
	
	private static final String CMD_DOWNLOAD 						= "C4 00 00";
	
	private static final String CMD_QUERY_CONTENT_LENGTH 			= "C6 00 00";
	private static final String CMD_QUERY_CAMERA_TIME 				= "C6 00 03";
	private static final String CMD_QUERY_BATTERY_LEVEL 			= "C6 00 06";
	
	private static final String CMD_TAKE_PICTURE 					= "C0 00 00";
	
	private static final String CMD_SET_CAMERA_TIME 				= "C0 00 04";
	
	
	public static final byte[] TEST_CMD_TAKE_PICTURE = hexStringToByteArray("AF55AAFA0000000000000001C0000000000000000000000000000000");
	public static final byte[] TEST_CMD_TIME = hexStringToByteArray("AF55AAFA0000FFFF00000001C6000600000000000000000000000000");
	
	
	
	public static byte[] takePicture() {
		
		return buildCommand(0, buildFlag(false, true), CMD_TAKE_PICTURE, PARAMETER_NULL);
	}
	
	
	public static byte[] getCameraTime() {
		
		return buildCommand(30, buildFlag(false, true), CMD_QUERY_BATTERY_LEVEL, PARAMETER_NULL);
	}
			
	
	private static byte[] buildCommand(int contentByteLength, String flags, String command, String parameters) {
		
		String length = String.format("%08X", contentByteLength);		
		String message = MAGIC_NUMBER + length + flags + command + parameters;
		
		System.out.println(message.replaceAll("\\s",""));

		return hexStringToByteArray(message.replaceAll("\\s",""));
	}
	
	
	
	
	
	private static String buildFlag(Boolean isPlayload, Boolean isRequest) {
		String flag = "";
		
		if (isRequest) {
			flag += "0";
		} else {
			flag += "1";
		}
		
		if (isPlayload) {
			flag += "0";
		} else {
			flag += "1";
		}

		return TWO_BYTES_NULL + BYTE_NULL + flag; 
		
	}
			
	
	private static byte[] hexStringToByteArray(String s) {
		
		int len = s.length();
		byte[] data = new byte[len / 2];
		
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character
					.digit(s.charAt(i + 1), 16));
		}
		
		return data;
	}

}
