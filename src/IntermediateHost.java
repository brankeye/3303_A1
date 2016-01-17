import java.net.*;
import java.io.*;

public class IntermediateHost {

	private DatagramPacket sendPacket, receivePacket;
	private DatagramSocket receiveSocket, duplexSocket;
	
	IntermediateHost() {
		try {
			receiveSocket = new DatagramSocket(68);
			duplexSocket = new DatagramSocket();
	    } catch (SocketException se) {   // Can't create the socket.
	    	se.printStackTrace();
	    	System.exit(1);
	    }
	}
	
	public static void main(String[] args) {
		IntermediateHost host = new IntermediateHost();
		host.run();
	}

	public void run() {
		while(true) {
			receiveAndEcho();
		}
	}
	
	public void receiveAndEcho() {
		byte data[] = new byte[100];
		data = receive();
		send(data, 69);
		data = receive();
		send(data, receivePacket.getPort());
	}
	
	public byte[] receive() {
		byte data[] = new byte[100];
		receivePacket = new DatagramPacket(data, data.length);
		System.out.println("IHost: Waiting for Packet.\n");
		
		try {
			System.out.println("Waiting..."); // so we know we're waiting
			receiveSocket.receive(receivePacket);
		} catch(IOException e) {
			System.out.print("IO Exception: likely:");
	        System.out.println("Receive Socket Timed Out.\n" + e);
			e.printStackTrace();
			System.exit(1);
		}
		
		System.out.println("IHost: Packet received:");
	    System.out.println("From host: " + receivePacket.getAddress());
	    System.out.println("Host port: " + receivePacket.getPort());
	    int len = receivePacket.getLength();
	    System.out.println("Length: " + len);
	    System.out.print("Containing: " );
	    
	    // Form a String from the byte array.
	    String received = new String(data,0,len);   
	    System.out.println(received + "\n");
	    
	    return data;
	}
	
	public void send(byte data[], int port) {
		sendPacket = new DatagramPacket(data, receivePacket.getLength(), 
				receivePacket.getAddress(), port);
		try {
			duplexSocket.send(sendPacket);
		} catch (IOException e) {
	        e.printStackTrace();
	        System.exit(1);
	    }
		
		// print more log info
		System.out.println("IHost: Sending packet:");
	    System.out.println("To host: " + sendPacket.getAddress());
	    System.out.println("Destination host port: " + sendPacket.getPort());
	    int len = sendPacket.getLength();
	    System.out.println("Length: " + len);
	    System.out.print("Containing: ");
	    System.out.println(new String(sendPacket.getData(),0,len)); // or could print "s"
	    
	    // Send the datagram packet to the server via the send/receive socket. 
	    try {
	       duplexSocket.send(sendPacket);
	    } catch (IOException e) {
	       e.printStackTrace();
	       System.exit(1);
	    }
	    System.out.println("IHost: Packet sent.\n");
	}
}
