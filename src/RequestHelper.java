import java.io.*;

public class RequestHelper {
	public enum Format { WRQ, RRQ, DATA, ACK, ERROR };
	public enum Mode { NETASCII, OCTET };
	private static Mode defaultMode = Mode.NETASCII;
	
	public static byte[] getByteArray(Format format, String filename) {
		try {
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			stream.write(0);
			switch (format) {
				case RRQ: stream.write(1); break;
				case WRQ: stream.write(2); break;
				case DATA: stream.write(3); break;
				case ACK: stream.write(4); break;
				case ERROR: stream.write(5); break;
				default: return new byte[] {-1};
			}
			
			stream.write(filename.getBytes());
			stream.write(0);
			stream.write(defaultMode.toString().getBytes());
			stream.write(0);
			
			return stream.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// if for some reason getting a byte array fails...
		return new byte[] {-1};
	}
	
	// get a string form of the request with default mode
	public static String getString(byte[] data, int length) {
		// StringBuffer is thread safe! WOOHOO
		StringBuffer buffer = new StringBuffer();
		String invalid = "INVALID";
		
		// get format
		if(data[0] == 0) { buffer.append(0); }
		else { return invalid; }
		
		for(int i = 1; i < Format.values().length + 1; ++i) {
			if(data[1] == i) { buffer.append(i); }
		}
		if(buffer.length() == 1) { return invalid; }
		
		// get filename and mode
		for(int i = 2; i < length; ++i) {
			if(data[i] == 0) { buffer.append(0); }
			else { buffer.append((char)data[i]); };
		}
		
		return buffer.toString();
	}
}
