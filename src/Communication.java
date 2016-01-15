import java.io.*;
import java.net.*;

public class Communication {
	
	private String name;
	private DatagramPacket sendPacket, receivePacket;
	private DatagramSocket sendReceiveSocket;
	
	public Communication(String t_name) {
		name = t_name; // Client, IHost, Server
	}
	
	public void sendAndReceive() {
		sendNewPacket("WRQ", "test.txt", "netascii");
	}
	
	public void sendNewPacket(String type, String str, String mode) {
		String fullMessage = "";
		String typeBytes = "";
		switch (type) {
			case "WRQ": typeBytes = "01";
			case "RRQ": typeBytes = "02";
			case "DATA": typeBytes = "03";
			case "ACK": typeBytes = "04";
			case "ERROR": typeBytes = "05";
		}
		fullMessage = typeBytes + str + mode;
		byte msg[] = fullMessage.getBytes();
		
		try {
			sendPacket = new DatagramPacket(msg, msg.length, 
											InetAddress.getLocalHost(), 5000);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		// print more log info
		System.out.println(name + ": sending a packet containing:\n" + str);
		System.out.println(name + ": Sending packet:");
	    System.out.println("To host: " + sendPacket.getAddress());
	    System.out.println("Destination host port: " + sendPacket.getPort());
	    int len = sendPacket.getLength();
	    System.out.println("Length: " + len);
	    System.out.print("Containing: ");
	    System.out.println(new String(sendPacket.getData(),0,len)); // or could print "s"
	    
	    // Send the datagram packet to the server via the send/receive socket. 
	    try {
	       sendReceiveSocket.send(sendPacket);
	    } catch (IOException e) {
	       e.printStackTrace();
	       System.exit(1);
	    }
	    System.out.println(name + ": Packet sent.\n");
	}
	
	public void receiveNewPacket() {
		byte data[] = new byte[100];
		receivePacket = new DatagramPacket(data, data.length);
		
		try {
			sendReceiveSocket.receive(receivePacket);
		} catch(IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		// Process the received datagram.
	    System.out.println(name + ": Packet received:");
	    System.out.println("From host: " + receivePacket.getAddress());
	    System.out.println("Host port: " + receivePacket.getPort());
	    int len = receivePacket.getLength();
	    System.out.println("Length: " + len);
	    System.out.print("Containing: ");

	    // Form a String from the byte array.
	    String received = new String(data,0,len);   
	    System.out.println(received);

	    // We're finished, so close the socket.
	    sendReceiveSocket.close();
	}
	
	public String getName() { return name; }
}
