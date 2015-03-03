package lytro.old;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

import lytro.old.LytroCommands;

public class LytroEmission implements Runnable {

	private OutputStream os = null;
	private String message = null;
	private Scanner sc = null;
	private Socket socket = null;
	public static Thread t2;

	public LytroEmission(Socket s) {
		socket = s;
	}

	public void run() {

		// TODO where to put os.close() ?
		try {
			os = socket.getOutputStream();

		} catch (IOException e) {
			System.err.println("Le serveur distant s'est déconnecté !");
		}

		sc = new Scanner(System.in);

		while (true) {
			System.out.println("Your command :");
			message = sc.nextLine();
			

			if (message.equals("takepic")) {

				try {
					System.out.println("User > takePicture()");


					os.write(LytroCommands.TEST_CMD_TIME);
					os.flush();

				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

	}
}