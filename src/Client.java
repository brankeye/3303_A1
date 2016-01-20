import java.net.*;
import java.io.*;

public class Client {

	private DatagramPacket sendPacket, receivePacket;
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
		/*
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
	
	public void sendNewPacket(RequestHelper.Format format, String filename) {
		try {
			byte msg[] = RequestHelper.getByteArray(format, filename);
			sendPacket = new DatagramPacket(msg, msg.length, 
											InetAddress.getLocalHost(), sendPort);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		// print more log info
		System.out.println("Client: sending a packet containing:\n" + filename);
		System.out.println("Client: Sending packet:");
	    System.out.println("To host: " + sendPacket.getAddress());
	    System.out.println("Destination host port: " + sendPacket.getPort());
	    int len = sendPacket.getLength();
	    System.out.println("Length: " + len);
	    System.out.print("Containing: ");
	    System.out.println(RequestHelper.getString(sendPacket.getData(), len) + "\n"); // or could print "s"
	    
	    // Send the datagram packet to the server via the send/receive socket. 
	    try {
	       duplexSocket.send(sendPacket);
	    } catch (IOException e) {
	       e.printStackTrace();
	       System.exit(1);
	    }
	    System.out.println("Client: Packet sent.\n");
	}
	
	public void receiveNewPacket() {
		byte data[] = new byte[100];
		receivePacket = new DatagramPacket(data, data.length);
		
		try {
			duplexSocket.receive(receivePacket);
		} catch(IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		// Process the received datagram.
	    System.out.println("Client: Packet received:");
	    System.out.println("From host: " + receivePacket.getAddress());
	    System.out.println("Host port: " + receivePacket.getPort());
	    int len = receivePacket.getLength();
	    System.out.println("Length: " + len);
	    System.out.print("Containing: ");

	    // Form a String from the byte array.
	    String received = RequestHelper.getString(receivePacket.getData(), len) + "\n";   
	    System.out.println(received);
	}
}
