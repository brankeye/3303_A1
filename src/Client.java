

public class Client {

	Communication comms;
	
	Client() {
		comms = new Communication("Client");
	}
	
	public void sendAndReceive() {
		comms.sendAndReceive("WRQ", "test.txt");
		comms.sendAndReceive("RRQ", "test.txt");
	}
	
	public static void main(String[] args) {
		Client client = new Client();
		client.sendAndReceive();
		System.out.println("Client running...");
	}

	
}
