import java.io.*;
import java.nio.charset.StandardCharsets;

public class Request {
	public enum Mode { NETASCII, OCTET };
	
	public Request() {
	}
	
	public static byte[] getByteArray(String req, String str, Mode mode) {
		try {
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			stream.write(0);
			switch (req) {
				case "WRQ": stream.write(1); break;
				case "RRQ": stream.write(2); break;
				case "DATA": stream.write(3); break;
				case "ACK": stream.write(4); break;
				case "ERROR": stream.write(5); break;
				default: break;
			}
			
			stream.write(str.getBytes());
			stream.write(0);
			stream.write(mode.toString().getBytes());
			stream.write(0);
			
			byte msg[] = stream.toByteArray();
			return msg;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
