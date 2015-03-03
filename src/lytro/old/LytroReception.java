package lytro.old;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import static lytro.util.Constants.LYTRO_SPEAKING;


public class LytroReception implements Runnable {

	private int message = 0;
	private Socket socket = null;
	private BufferedReader in = null;
	private char[] ch = new char[32];
	

	public LytroReception(Socket s) {
		socket = s;
	}

        @Override
	public void run() {

		try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

		} catch (IOException e) {
			System.err.println("Server disconnected !");
		}

		while (true) {
			try {

				message = in.read(ch);
				
				for (char c : ch) {
					String hex = String.format("%02x", (int) c);
					System.out.println(hex);
				}
				System.out.println(LYTRO_SPEAKING + message + ", ");

//				if (!message.contains(LytroCallbacks.CB_HEARTBEAT_TICK)) {
//					System.out.println(LytroCallbacks.LYTRO_SPEAKING + message);
//				}

			} catch (IOException e) {

				e.printStackTrace();
			}
		}
	}

}