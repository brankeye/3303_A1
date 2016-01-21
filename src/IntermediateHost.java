import java.net.*;
import java.io.*;

public class IntermediateHost {
	
	private DatagramSocket receiveSocket, duplexSocket;
	private static int iHostPort = 68;
	private static int  serverPort = 69;
	private InetAddress serverAddress;
	private boolean running;
	
	IntermediateHost() {
		try {
			receiveSocket = new DatagramSocket(iHostPort);
			duplexSocket = new DatagramSocket();
	    } catch (SocketException se) {   // Can't create the socket.
	    	se.printStackTrace();
	    	System.exit(1);
	    }
		running = true;
		
		try {
			serverAddress = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
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
	private void relay() {
		DatagramPacket clientPacket = receiveClient();
		sendServer(new DatagramPacket(clientPacket.getData(), clientPacket.getLength(), 
									  serverAddress, serverPort));
		
		DatagramPacket serverPacket = receiveServer();
		sendClient(new DatagramPacket(serverPacket.getData(), serverPacket.getLength(),
									  clientPacket.getAddress(), clientPacket.getPort()));
	}
	
	private DatagramPacket receiveClient() {
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
  	    readReceivePacket(receivePacket);
	    
	    return receivePacket;
	}
	
	private DatagramPacket receiveServer() {
		byte data[] = new byte[100];
		DatagramPacket receivePacket = new DatagramPacket(data, data.length);
		System.out.println("IHost: waiting for a packet...\n");
		
		try {
			duplexSocket.receive(receivePacket);
		} catch(IOException e) {
			System.out.print("IO Exception: likely:");
	        System.out.println("Receive Socket Timed Out.\n" + e);
			e.printStackTrace();
			System.exit(1);
		}
  	    readReceivePacket(receivePacket);
	    
	    return receivePacket;
	}
	
	private void sendClient(DatagramPacket sendPacket) {
		readSendPacket(sendPacket);
	    
	    // Send the datagram packet to the server via the send/receive socket. 
	    try {
	    	DatagramSocket sendSocket = new DatagramSocket();
			sendSocket.send(sendPacket);
			sendSocket.close();
	    } catch (IOException e) {
	       e.printStackTrace();
	       System.exit(1);
	    }
	    System.out.println("IHost: packet sent.\n");
	}
	
	private void sendServer(DatagramPacket sendPacket) {
		readSendPacket(sendPacket);
	    
	    // Send the datagram packet to the server via the send/receive socket. 
	    try {
	       duplexSocket.send(sendPacket);
	    } catch (IOException e) {
	       e.printStackTrace();
	       System.exit(1);
	    }
	    System.out.println("IHost: packet sent.\n");
	}
	
	private void readSendPacket(DatagramPacket sendPacket) {
		// print log
		System.out.println("IHost: sending a packet...");
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
 		System.out.println("IHost: receiving a packet...");
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
 	    System.out.println("IHost: packet received.\n");
	}
}
