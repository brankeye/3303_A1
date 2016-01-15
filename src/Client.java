

public class Client {

	Communication comms;
	
	Client() {
		comms = new Communication("Client");
	}
	
	public void sendAndReceive() {
		String msg = "Message from client.";
		System.out.println("Client: sending a packet containing:\n" + msg);
		
		
	}
	
	public static void main(String[] args) {
		Client client = new Client();
		System.out.println("Client running...");
	}

	
}
