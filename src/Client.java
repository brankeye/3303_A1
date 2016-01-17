import java.net.*;
import java.io.*;

public class Client {

	private DatagramPacket sendPacket, receivePacket;
	private DatagramSocket duplexSocket;
	
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
		sendAndReceive("WRQ", "test.txt");
		sendAndReceive("RRQ", "test.txt");
	}

	public void sendAndReceive(String req, String text) {
		sendNewPacket(req, text);
		receiveNewPacket();
	}
	
	public void sendNewPacket(String type, String str) {
		try {
			byte msg[] = Request.getByteArray(type, str, Request.Mode.NETASCII);
			sendPacket = new DatagramPacket(msg, msg.length, 
											InetAddress.getLocalHost(), 68);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		// print more log info
		System.out.println("Client: sending a packet containing:\n" + str);
		System.out.println("Client: Sending packet:");
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
	    String received = new String(data,0,len);   
	    System.out.println(received);

	    // We're finished, so close the socket.
	    duplexSocket.close();
	}
}
