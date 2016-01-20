import java.net.*;
import java.io.*;

public class Server {

	private DatagramPacket sendPacket, receivePacket;
	private DatagramSocket sendSocket, receiveSocket;
	private static int thisPort = 69;
	private static int sendPort = 68;
	private boolean running;
	
	Server() {
		try {
			receiveSocket = new DatagramSocket(thisPort);
         
			// to test socket timeout (2 seconds)
			//receiveSocket.setSoTimeout(2000);
		} catch (SocketException se) {
			se.printStackTrace();
			System.exit(1);
		} 
		running = true;
	}
	
	public static void main(String[] args) {
		Server server = new Server();
		server.run();
	}

	public void run() {
		while(running) {
			receiveAndEcho();
		}
	}
	
	public void receiveAndEcho() {
		byte data[] = new byte[100];
		data = receive();
		send(data, sendPort);
	}
	
	public byte[] receive() {
		byte data[] = new byte[100];
		receivePacket = new DatagramPacket(data, data.length);
		System.out.println("Server: Waiting for Packet.\n");

		// Block until a datagram packet is received from receiveSocket.
		try {        
			System.out.println("Waiting..."); // so we know we're waiting
			receiveSocket.receive(receivePacket);
		} catch (IOException e) {
			System.out.print("IO Exception: likely:");
			System.out.println("Receive Socket Timed Out.\n" + e);
			e.printStackTrace();
			System.exit(1);
		}

		// Process the received datagram.
		System.out.println("Server: Packet received:");
		System.out.println("From host: " + receivePacket.getAddress());
		System.out.println("Host port: " + receivePacket.getPort());
		int len = receivePacket.getLength();
		System.out.println("Length: " + len);
		System.out.print("Containing: " );

		// Form a String from the byte array.
		String received = RequestHelper.getString(receivePacket.getData(), len);  
		System.out.println(received + "\n");
		
		// Parse the packet
		//String pattern = "^[a-zA-Z0-9~@#$%^&*:;<>.,/}{+-]*$";
		
		
		return data;
	}
	
	public void send(byte data[], int destPort) {
		try {
			sendPacket = new DatagramPacket(data, receivePacket.getLength(),
	                           receivePacket.getAddress(), destPort);
	
			System.out.println( "Server: Sending packet:");
			System.out.println("To host: " + sendPacket.getAddress());
			System.out.println("Destination host port: " + sendPacket.getPort());
			int len = sendPacket.getLength();
			System.out.println("Length: " + len);
			System.out.print("Containing: ");
			System.out.println(RequestHelper.getString(sendPacket.getData(), len) + "\n");
			// or (as we should be sending back the same thing)
			// System.out.println(received); 
	    
			// Send the datagram packet to the client via the send socket. 
			sendSocket = new DatagramSocket();
			sendSocket.send(sendPacket);
			sendSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		System.out.println("Server: packet sent");
	}
}
