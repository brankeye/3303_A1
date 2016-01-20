import java.net.*;
import java.io.*;

public class IntermediateHost {
	
	private DatagramSocket receiveSocket, duplexSocket;
	public static int thisPort = 68;
	public static int serverPort = 69;
	private boolean running;
	
	IntermediateHost() {
		try {
			receiveSocket = new DatagramSocket(thisPort);
			duplexSocket = new DatagramSocket();
	    } catch (SocketException se) {   // Can't create the socket.
	    	se.printStackTrace();
	    	System.exit(1);
	    }
		running = true;
	}
	
	public static void main(String[] args) {
		IntermediateHost host = new IntermediateHost();
		host.run();
	}

	public void run() {
		while(running) {
			relay();
		}
	}
	
	// handle comms from client to server
	public void relay() {
		DatagramPacket clientPacket = receive();
		try {
		send(new DatagramPacket(clientPacket.getData(), clientPacket.getLength(), 
								InetAddress.getLocalHost(), serverPort));
		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.exit(1);
		}
		DatagramPacket serverPacket = receive();
		send(new DatagramPacket(serverPacket.getData(), serverPacket.getLength(),
								clientPacket.getAddress(), clientPacket.getPort()));
	}
	
	public DatagramPacket receive() {
		byte data[] = new byte[100];
		DatagramPacket receivePacket = new DatagramPacket(data, data.length);
		System.out.println("IHost: waiting for a packet...\n");
		
		try {
			receiveSocket.receive(receivePacket);
		} catch(IOException e) {
			System.out.print("IO Exception: likely:");
	        System.out.println("Receive Socket Timed Out.\n" + e);
			e.printStackTrace();
			System.exit(1);
		}
	    
	    // print log
  		System.out.println("IHost: receiving a packet...");
  	    System.out.println("From host: " + receivePacket.getAddress());
  	    System.out.println("Host port: " + receivePacket.getPort());
  	    String reqString = RequestHelper.getString(receivePacket.getData(), receivePacket.getLength());
  	    byte reqBytes[] =  receivePacket.getData();
  	    System.out.print("String: '" + reqString + "'\n");
  	    System.out.print("Bytes:  '" + reqBytes  + "'\n");	    
  	    System.out.println("IHost: packet received.\n");
	    
	    return receivePacket;
	}
	
	public void send(DatagramPacket sendPacket) {
		// print log
		System.out.println("IHost: sending a packet...");
	    System.out.println("To host: " + sendPacket.getAddress());
	    System.out.println("Destination host port: " + sendPacket.getPort());
	    String reqString = RequestHelper.getString(sendPacket.getData(), sendPacket.getLength());
	    byte reqBytes[] =  sendPacket.getData();
	    System.out.print("String: '" + reqString + "'\n");
	    System.out.print("Bytes:  '" + reqBytes  + "'\n");
	    
	    // Send the datagram packet to the server via the send/receive socket. 
	    try {
	       duplexSocket.send(sendPacket);
	    } catch (IOException e) {
	       e.printStackTrace();
	       System.exit(1);
	    }
	    System.out.println("IHost: packet sent.\n");
	}
}
