import java.net.*;
import java.io.*;

public class Server {

	private DatagramPacket sendPacket, receivePacket;
	private DatagramSocket sendSocket, receiveSocket;
	
	Server() {
		try {
			sendSocket = new DatagramSocket();
			receiveSocket = new DatagramSocket(69);
         
			// to test socket timeout (2 seconds)
			//receiveSocket.setSoTimeout(2000);
		} catch (SocketException se) {
			se.printStackTrace();
			System.exit(1);
		} 
	}
	
	public static void main(String[] args) {
		Server server = new Server();
		server.run();
	}

	public void run() {
		while(true) {
			receiveAndEcho();
		}
	}
	
	public void receiveAndEcho() {
		receive();
		send();
	}
	
	public void receive() {
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
		String received = new String(data,0,len);   
		System.out.println(received + "\n");
		
		// Parse the packet
		
	}
	
	public void send() {
		// Slow things down (wait 5 seconds)
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e ) {
			e.printStackTrace();
			System.exit(1);
		}

		sendPacket = new DatagramPacket(data, receivePacket.getLength(),
                           receivePacket.getAddress(), receivePacket.getPort());

		System.out.println( "Server: Sending packet:");
		System.out.println("To host: " + sendPacket.getAddress());
		System.out.println("Destination host port: " + sendPacket.getPort());
		len = sendPacket.getLength();
		System.out.println("Length: " + len);
		System.out.print("Containing: ");
		System.out.println(new String(sendPacket.getData(),0,len));
		// or (as we should be sending back the same thing)
		// System.out.println(received); 
    
		// Send the datagram packet to the client via the send socket. 
		try {
			sendSocket.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		System.out.println("Server: packet sent");

		// We're finished, so close the sockets.
		sendSocket.close();
		receiveSocket.close();
	}
}
