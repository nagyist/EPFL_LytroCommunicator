package lytro.communication;

import java.net.Socket;

public class LytroCommunication {
	
	private static final String HOST_NAME = "10.100.1.1";
	private static final int CALLBACK_PORT = 5677;
	private static final int CONTROL_PORT = 5678;

	public static Socket callbackSocket = null;
	public static Socket controlSocket = null;
	
	public static Thread callbackThread;
	public static Thread controlThreadOUT, controlThreadIN;

	
	public static void main(String[] args) {
            CallBackServer cs = new CallBackServer(CALLBACK_PORT, HOST_NAME);            
            new Thread(cs).start();
            
            LytroServer ls = new LytroServer(HOST_NAME, CONTROL_PORT);            
            new Thread(ls).start();
	}

}