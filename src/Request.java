import java.io.*;

public class Request {
	public enum Format { RRQ, WRQ, DATA, ACK, ERROR, BADFORMAT };
	public enum Mode { NETASCII, OCTET, BADMODE };
	public static Mode defaultMode = Mode.NETASCII;
	
	private byte[] bytes;
	private int length;
	
	public Request(Format format_t, String filename_t) {
		bytes = makeByteArray(format_t, filename_t, defaultMode);
		length = bytes.length;
	}
	
	public Request(Format format_t, String filename_t, Mode mode_t) {
		bytes = makeByteArray(format_t, filename_t, mode_t);
		length = bytes.length;
	}
	
	public Request(byte[] data, int length_t) {
		bytes = data;
		length = length_t;
	}
	
	// get a string form of the request
	public String getString() {
		Format format = getFormat();
		
		if(format == Format.RRQ || format == Format.WRQ) {
			// StringBuffer is thread safe! WOOHOO
			StringBuffer buffer = new StringBuffer();
			
			// get format
			buffer.append(0);
			buffer.append(getFormat().ordinal() + 1);
			
			// get filename
			buffer.append(getFilename());
			buffer.append(0);
			
			// get mode
			buffer.append(getMode().toString());
			buffer.append(0);
			
			return buffer.toString();
		}
		
		if(format == Format.DATA || format == Format.ACK) {
			StringBuffer buffer = new StringBuffer();
			int i = 0;
			while(i < length) {
				buffer.append(bytes[i++]);
			}
			return buffer.toString();
		}
		
		return format.toString(); // error return
	}
	
	// returns null in the case of an invalid request
	private byte[] makeByteArray(Format format, String filename, Mode mode) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		try {
			stream.write(0);
			stream.write(format.ordinal() + 1);
			
			stream.write(filename.getBytes());
			stream.write(0);
			stream.write(mode.toString().getBytes());
			stream.write(0);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return stream.toByteArray();
	}
	
	// getFormat, getFilename, getMode all preserve request validity
	public Format getFormat() {
		// get format
		if(bytes[0] != 0) { return Format.BADFORMAT; }
		
		for(int i = 0; i < Format.values().length; ++i) {
			if(bytes[1] == i + 1) { return Format.values()[i]; }
		}
		return Format.BADFORMAT;
	}
	
	public String getFilename() {
		StringBuffer buffer = new StringBuffer();
		for(int i = 2; i < bytes.length; ++i) {
			if(bytes[i] == 0) { return buffer.toString(); }
			else { buffer.append((char)bytes[i]); };
		}
		return buffer.toString();
	}
	
	public Mode getMode() {
		if(bytes[bytes.length - 1] != 0) { return Mode.BADMODE; }
		boolean modeIndex = false;
		StringBuffer buffer = new StringBuffer();
		for(int i = 2; i < bytes.length; ++i) {
			if(!modeIndex && bytes[i] == 0) { modeIndex = true; }
			else if(modeIndex && bytes[i] != 0) { buffer.append((char)bytes[i]); }
		}
		
		try {
			return Mode.valueOf(buffer.toString());
		} catch(IllegalArgumentException e) {
			e.printStackTrace();
		}
		return Mode.BADMODE;
	}
	
	public boolean isValid() {
		// verify format
		if(bytes[0] != 0) { return false; }
		if(bytes[1] == Format.BADFORMAT.ordinal() + 1) { return false; }
		boolean validFormat = false;
		for(int i = 0; i < Format.values().length; ++i) {
			if(bytes[1] == i + 1) { validFormat = true; }
		}
		if(!validFormat) { return false; }
		
		// verify filename and mode
		boolean modeIndex = false;
		int filenameLength = 0;
		StringBuffer modeBuffer = new StringBuffer();
		for(int i = 2; i < bytes.length; ++i) {
			if(!modeIndex && bytes[i] == 0) { modeIndex = true; }
			else if(modeIndex && bytes[i] != 0) { modeBuffer.append((char)bytes[i]); }
			else { filenameLength++; }
		}
		if(filenameLength <= 0) { return false; }
		
		Mode mode_t = Mode.BADMODE;
		try {
			mode_t = Mode.valueOf(modeBuffer.toString());
		} catch(IllegalArgumentException e) {
			e.printStackTrace();
		}
		
		switch(mode_t) {
			case NETASCII: break;
			case OCTET:    break;
			case BADMODE:  return false;
			default:       return false;
		}
		
		return true;
	}
	
	public byte[] getByteArray() { return bytes;  }
	public int    getLength()    { return length; }
}
