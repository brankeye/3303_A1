import java.io.*;
import java.net.*;

public class Communication {
	
	private String name;
	private DatagramPacket sendPacket, receivePacket;
	private DatagramSocket sendReceiveSocket;
	private String mode;
	
	public Communication(String t_name) {
		name = t_name; // Client, IHost, Server
		mode = "netascii";
		
		try {
	       sendReceiveSocket = new DatagramSocket();
	    } catch (SocketException se) {   // Can't create the socket.
	       se.printStackTrace();
	       System.exit(1);
	    }
	}
	
	public void sendAndReceive(String req, String text) {
		sendNewPacket(req, text, mode);
		receiveNewPacket();
	}
	
	public void sendNewPacket(String type, String str, String mode) {
		try {
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			stream.write(0);
			switch (type) {
				case "WRQ": stream.write(1); break;
				case "RRQ": stream.write(2); break;
				case "DATA": stream.write(3); break;
				case "ACK": stream.write(4); break;
				case "ERROR": stream.write(5); break;
				default: break;
			}
			
			stream.write(str.getBytes());
			stream.write(0);
			stream.write(mode.getBytes());
			stream.write(0);
			
			byte msg[] = stream.toByteArray();
			
				sendPacket = new DatagramPacket(msg, msg.length, 
												InetAddress.getLocalHost(), 68);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
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
