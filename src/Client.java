import java.net.*;
import java.io.*;

public class Client {
	
	private DatagramSocket duplexSocket;
	private static int sendPort = 68;
	
	Client() {
		try {
	       duplexSocket = new DatagramSocket();
	    } catch (SocketException se) {   // Can't create the socket.
	       se.printStackTrace();
	       System.exit(1);
	    }
	}
	
	public static void main(String[] args) {
		Client client = new Client();
		client.run();
	}
	
	public void run() {
		read("testRead1.txt");
		write("testWrite1.txt");
		
		/*
		read("testRead1.txt");
		read("testRead2.txt");
		read("testRead3.txt");
		read("testRead4.txt");
		read("testRead5.txt");
		
		write("testWrite1.txt");
		write("testWrite2.txt");
		write("testWrite3.txt");
		write("testWrite4.txt");
		write("testWrite5.txt");
		*/
		// some kind of invalid request here...
	}
	
	public void read(String filename) {
		// create a write request to send
		sendNewPacket(RequestHelper.Format.RRQ, filename);
		receiveNewPacket();
	}
	
	public void write(String filename) {
		// create a write request to send
		sendNewPacket(RequestHelper.Format.WRQ, filename);
		receiveNewPacket();
	}
	
	public void receiveNewPacket() {
		byte data[] = new byte[100];
		DatagramPacket receivePacket = new DatagramPacket(data, data.length);
		
		try {
			duplexSocket.receive(receivePacket);
		} catch(IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		// print log
 		System.out.println("Client: receiving a packet...");
 	    System.out.println("From host: " + receivePacket.getAddress());
 	    System.out.println("Host port: " + receivePacket.getPort());
 	    String reqString = RequestHelper.getString(receivePacket.getData(), receivePacket.getLength());
 	    String reqBytes  = new String(receivePacket.getData(), 0, receivePacket.getLength());
 	    System.out.print("String: '" + reqString + "'\n");
 	    System.out.print("Bytes:  '" + reqBytes  + "'\n");	    
 	    System.out.println("Client: packet received.\n");
	}
	
	public void sendNewPacket(RequestHelper.Format format, String filename) {
		DatagramPacket sendPacket = null;
		try {
			byte msg[] = RequestHelper.getByteArray(format, filename);
			sendPacket = new DatagramPacket(msg, msg.length, 
											InetAddress.getLocalHost(), sendPort);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		// print log
		System.out.println("Client: sending a packet...");
	    System.out.println("To host: " + sendPacket.getAddress());
	    System.out.println("Destination host port: " + sendPacket.getPort());
	    String reqString = RequestHelper.getString(sendPacket.getData(), sendPacket.getLength());
	    String reqBytes  = new String(sendPacket.getData(), 0, sendPacket.getLength());
	    System.out.print("String: '" + reqString + "'\n");
	    System.out.print("Bytes:  '" + reqBytes  + "'\n");
	    
	    // Send the datagram packet to the server via the send/receive socket. 
	    try {
	       duplexSocket.send(sendPacket);
	    } catch (IOException e) {
	       e.printStackTrace();
	       System.exit(1);
	    }
	    System.out.println("Client: packet sent.\n");
	}
}
