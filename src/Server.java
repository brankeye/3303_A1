import java.net.*;
import java.io.*;

public class Server {

	private DatagramSocket receiveSocket;
	private static int thisPort = 69;
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
		DatagramPacket receivedPacket = receive();
		reply(receivedPacket);
	}
	
	public DatagramPacket receive() {
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

		// print log
  		System.out.println("Server: receiving a packet...");
  	    System.out.println("From host: " + receivePacket.getAddress());
  	    System.out.println("Host port: " + receivePacket.getPort());
  	    String reqString = RequestHelper.getString(receivePacket.getData(), receivePacket.getLength());
  	    byte reqBytes[] =  receivePacket.getData();
  	    System.out.print("String: '" + reqString + "'\n");
  	    System.out.print("Bytes:  '" + reqBytes.toString()  + "'\n");	    
  	    System.out.println("Server: packet received.\n");
		
		return receivePacket;
	}
	
	public void reply(DatagramPacket receivedPacket) {
		// verify the packet
  	    byte serverMsg[] = {};
  	    try {
  	    	if(!RequestHelper.isValid(receivedPacket.getData(), receivedPacket.getLength())) { throw new IllegalStateException(); }
  	    	RequestHelper.Format format_t = RequestHelper.getFormat(receivedPacket.getData(), receivedPacket.getLength());
  	    	switch(format_t) {
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

		// print log
		System.out.println("Server: sending a packet...");
	    System.out.println("To host: " + sendPacket.getAddress());
	    System.out.println("Destination host port: " + sendPacket.getPort());
	    String reqString = RequestHelper.getString(sendPacket.getData(), sendPacket.getLength());
	    byte reqBytes[] =  sendPacket.getData();
	    System.out.print("String: '" + reqString + "'\n");
	    System.out.print("Bytes:  '" + reqBytes.toString()  + "'\n");
	    
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
}
