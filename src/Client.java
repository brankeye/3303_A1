import java.net.*;
import java.io.*;

public class Client {
	
	private DatagramSocket duplexSocket; // socket for sending and receiving
	private static int  sendPort = 68;
	private InetAddress serverAddress;
	
	Client() {
		try {
	       duplexSocket = new DatagramSocket();
	       serverAddress = InetAddress.getLocalHost();
	    } catch (SocketException se) {   // Can't create the socket.
	       se.printStackTrace();
	       System.exit(1);
	    } catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		Client client = new Client();
		client.run();
	}
	
	public void run() {
		read("testRead1.txt");
		write("testWrite1.txt");
		read("testRead2.txt");
		write("testWrite2.txt");
		read("testRead3.txt");
		write("testWrite3.txt");
		read("testRead4.txt");
		write("testWrite4.txt");
		read("testRead5.txt");
		write("testWrite5.txt");
		
		invalidRequest();
	}
	
	// send a read request with the given filename to the server
	private void read(String filename) {
		// create a write request to send
		sendNewPacket(Request.Format.RRQ, filename);
		receiveNewPacket();
	}
	
	// send a write request with the given filename to the server
	private void write(String filename) {
		// create a write request to send
		sendNewPacket(Request.Format.WRQ, filename);
		receiveNewPacket();
	}
	
	// send an invalid request to the server
	private void invalidRequest() {
		// send bad request with empty filename
		sendNewPacket(Request.Format.BADFORMAT, "");
		receiveNewPacket();
	}
	
	// this handles the reception of incoming packets
	private void receiveNewPacket() {
		byte data[] = new byte[100];
		DatagramPacket receivePacket = new DatagramPacket(data, data.length);
		
		try {
			duplexSocket.receive(receivePacket);
		} catch(IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		readReceivePacket(receivePacket);
	}
	
	// sends a new packet with the given format (RRQ/WRQ/etc) and filename 
	private void sendNewPacket(Request.Format format, String filename) {
		Request req = new Request(format, filename);
		byte msg[] = req.getByteArray();
		DatagramPacket sendPacket = new DatagramPacket(msg, msg.length, 
										serverAddress, sendPort);
		
		readSendPacket(sendPacket);
	    
	    // Send the datagram packet to the server via the send/receive socket. 
	    try {
	       duplexSocket.send(sendPacket);
	    } catch (IOException e) {
	       e.printStackTrace();
	       System.exit(1);
	    }
	    System.out.println("Client: packet sent.\n");
	}
	
	// reads the contents of a send packet
	private void readSendPacket(DatagramPacket sendPacket) {
		// print log
		System.out.println("Client: sending a packet...");
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
	
	// reads the contents of a receive packet
	private void readReceivePacket(DatagramPacket receivePacket) {
		// print log
		Request req = new Request(receivePacket.getData(), receivePacket.getLength());
 		System.out.println("Client: receiving a packet...");
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
 	    System.out.println("Client: packet received.\n");
	}
}
