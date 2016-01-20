import java.net.*;
import java.io.*;

public class IntermediateHost {

	private DatagramPacket sendPacket, receivePacket;
	private DatagramSocket receiveSocket, duplexSocket;
	public static int thisPort = 68;
	public static int sendPort = 69;
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
		byte data[] = new byte[100];
		data = receive();
		int clientPort = receivePacket.getPort();
		send(data, sendPort);
		data = receive();
		send(data, clientPort);
	}
	
	public byte[] receive() {
		byte data[] = new byte[100];
		receivePacket = new DatagramPacket(data, data.length);
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
	    
	    return data;
	}
	
	public void send(byte data[], int destPort) {
		sendPacket = new DatagramPacket(data, receivePacket.getLength(), 
				receivePacket.getAddress(), destPort);
		
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
