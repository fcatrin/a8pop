package fcatrin.pop.utils;

public class BitStream {
	StringBuffer data = new StringBuffer();
	int readPosition = 0;
	
	public BitStream() {
		
	}
	
	public BitStream(byte[] data) {
		append(data);
	}
	
	public void append(int value, int nbits) {
		String s = Utils.bits2string(value, nbits);
		data.append(s);
	}
	
	public void append(String sBits) {
		data.append(sBits);
	}

	public void append(byte[] data) {
		for(byte b : data) {
			append(Utils.b2i(b), 8);
		}
	}
	
	public void appendHex(String s) {
		for(int i=0; i<s.length(); i+=2) {
			appendHexByte(s.substring(i));
		}
	}
	
	public void appendHexByte(String s) {
		int value = Utils.hex2int(s.charAt(0)) * 16 + Utils.hex2int(s.charAt(1));
		append(value, 8);
	}

	public String dump() {
		return data.toString();
	}
	
	public byte[] asBytes() {
		String s = dump();
		byte result[] = new byte[(s.length()+7)/8];
		for(int i=0; i<result.length; i++) {
			result[i] = Utils.string2bits(s, 8);
			if (s.length()>8) {
				s = s.substring(8);
			}
		}
		return result;
	}
	
	public String asString() {
		String s = dump();
		String result = "";
		int n = (s.length()+7)/8;
		for(int i=0; i<n; i++) {
			byte value = Utils.string2bits(s, 8);
			result+= String.format("%02x", Utils.b2i(value));
			if (s.length()>8) {
				s = s.substring(8);
			}
		}
		return result;
	}

	public int readWord() {
		if (readPosition + 16 > data.length()) return 0;
		int lsb = Utils.b2i(readByte());
		int msb = Utils.b2i(readByte());
		return lsb + 256 * msb;
	}

	public byte readByte() {
		return readBits(8);
	}

	public byte[] readBytes(int size) {
		byte result[] = new byte[size];
		for(int i=0; i<size; i++) {
			result[i] = readByte();
		}
		return result;
	}

	public byte readBits(int n) {
		return Utils.string2bits(readStringBits(n), n);
	}

	public String readStringBits(int n) {
		if (readPosition + n > data.length()) return "";
		String s = data.substring(readPosition, readPosition +n);
		readPosition += n;
		return s;
	}
	
	
	/*
	public static void main(String args[]) throws Exception {
		BitStream bs = new BitStream();
		bs.append(255, 8);
		bs.append(240, 8);
		bs.append(15, 8);
		bs.append(0x55, 8);
		bs.append(0xAA, 8);
		bs.append(0x80, 8);
		System.out.println(bs.dump());
		
		byte[] bytes = bs.asBytes();
		BitStream bs2 = new BitStream(bytes);
		System.out.println(bs2.dump());
	}
	 */
}
