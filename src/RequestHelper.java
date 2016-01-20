import java.io.*;

public class RequestHelper {
	public enum Format { RRQ, WRQ, DATA, ACK, ERROR };
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
		String invalid = null;
		
		// get format
		Format format_t = getFormat(data, length);
		if(format_t == Format.ERROR) { return invalid; }
		buffer.append(0);
		buffer.append(format_t.ordinal());
		
		// get filename
		String filename_t = getFilename(data, length);
		if(filename_t == null) { return invalid; } // this will return null if there is no 0 byte delimiter after filename
		buffer.append(filename_t);
		buffer.append(0);
		
		// get mode
		Mode mode_t = getMode(data, length);
		if(mode_t == null) { return invalid; } // this will return null if there is no 0 byte delimiter after mode
		buffer.append(mode_t.toString());
		buffer.append(0);
		
		return buffer.toString();
	}
	
	public static boolean isValid(byte[] data, int length) {
		if(getString(data, length) != null) { return true; }
		return false;
	}
	
	public static Format getFormat(byte[] data, int length) {
		// get format
		if(data[0] != 0) { return Format.ERROR; }
		
		for(int i = 0; i < Format.values().length; ++i) {
			if(data[1] == i + 1) { return Format.values()[i]; }
		}
		return Format.ERROR;
	}
	
	public static String getFilename(byte[] data, int length) {
		StringBuffer buffer = new StringBuffer();
		for(int i = 2; i < length; ++i) {
			if(data[i] == 0) { return buffer.toString(); }
			else { buffer.append((char)data[i]); };
		}
		return null;
	}
	
	public static Mode getMode(byte[] data, int length) {
		boolean modeIndex = false;
		StringBuffer buffer = new StringBuffer();
		for(int i = 2; i < length; ++i) {
			if(!modeIndex && data[i] == 0) { modeIndex = true; }
			else if(modeIndex && data[i] != 0) { buffer.append((char)data[i]); }
		}
		
		if(data[length - 1] != 0) { return null; }
		try {
			return Mode.valueOf(buffer.toString());
		} catch(IllegalArgumentException e) {
			e.printStackTrace();
		}
		return null;
	}
}
