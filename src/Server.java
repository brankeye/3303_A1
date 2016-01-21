import java.net.*;
import java.io.*;

public class Server {

	private DatagramSocket receiveSocket;
	private static int thisPort = 69;
	public static int hostPort = 68;
	private boolean running;
	
	Server() {
		try {
			receiveSocket = new DatagramSocket(thisPort);
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
	
	private void receiveAndEcho() {
		DatagramPacket receivedPacket = receive();
		reply(receivedPacket);
	}
	
	private DatagramPacket receive() {
		byte data[] = new byte[100];
		DatagramPacket receivePacket = new DatagramPacket(data, data.length);
		System.out.println("Server: waiting for a packet...\n");

		// Block until a datagram packet is received from receiveSocket.
		try {
			receiveSocket.receive(receivePacket);
		} catch (IOException e) {
			System.out.print("IO Exception: likely:");
			System.out.println("Receive Socket Timed Out.\n" + e);
			e.printStackTrace();
			System.exit(1);
		}

		readReceivePacket(receivePacket);
		
		return receivePacket;
	}
	
	private void reply(DatagramPacket receivedPacket) {
		// verify the packet
  	    byte serverMsg[] = {};
  	    try {
  	    	Request recReq = new Request(receivedPacket.getData(), receivedPacket.getLength());
  	    	if(!recReq.isValid()) { throw new IllegalStateException(); }
  	    	switch(recReq.getFormat()) {
  	    		case RRQ: serverMsg = new byte[] {0, 3, 0, 1}; break;
  	    		case WRQ: serverMsg = new byte[] {0, 4, 0, 0}; break;
  	    		default: throw new IllegalStateException();
  	    	}
  	    } catch(IllegalStateException e) {
  	    	e.printStackTrace();
  	    	System.exit(1);
  	    }
  	    
		DatagramPacket sendPacket = new DatagramPacket(serverMsg, serverMsg.length,
                           receivedPacket.getAddress(), receivedPacket.getPort());

		readSendPacket(sendPacket);
	    
	    try {
			// Send the datagram packet to the client via the send socket. 
			DatagramSocket sendSocket = new DatagramSocket();
			sendSocket.send(sendPacket);
			sendSocket.close();
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		System.out.println("Server: packet sent.\n");
	}
	
	private void readSendPacket(DatagramPacket sendPacket) {
		// print log
		System.out.println("Server: sending a packet...");
	    System.out.println("To host: " + sendPacket.getAddress());
	    System.out.println("Destination host port: " + sendPacket.getPort());
	    Request req = new Request(sendPacket.getData(), sendPacket.getLength());
	    String reqString = req.getString();
 	    byte reqBytes[]  = req.getByteArray();
 	    System.out.print("String: '" + reqString + "'\n");
	    System.out.print("Bytes:  '");
	    int i = 0;
	    while(i < req.getLength()) {
	    	System.out.print(reqBytes[i++]);
	    }
	    System.out.print("'\n");
	}
	
	private void readReceivePacket(DatagramPacket receivePacket) {
		// print log
		Request req = new Request(receivePacket.getData(), receivePacket.getLength());
 		System.out.println("Server: receiving a packet...");
 	    System.out.println("From host: " + receivePacket.getAddress());
 	    System.out.println("Host port: " + receivePacket.getPort());
 	    String reqString = req.getString();
	    byte reqBytes[]  = req.getByteArray();
	    System.out.print("String: '" + reqString + "'\n");
	    System.out.print("Bytes:  '");
	    int i = 0;
	    while(i < req.getLength()) {
	    	System.out.print(reqBytes[i++]);
	    }
	    System.out.print("'\n");    
 	    System.out.println("Server: packet received.\n");
	}
}
