package fcatrin.pop.utils;

public class Utils {
	public static int word(byte l, byte h) {
		return b2i(l) + 256 * b2i(h);
	}
	
	public static int b2i(byte b) {
		if (b>=0) return b;
		return b+256;
	}
	
	public static String bits2string(int value, int nbits) {
		// WARN: no validation of value size
		int pow = (int)Math.pow(2, nbits-1);
		StringBuffer buf = new StringBuffer();
		do {
			boolean bit = (value & pow) !=0; 
			buf.append(bit?"1":"0");
			pow = pow / 2;
		} while (pow>0);
		return buf.toString();
	}

	public static byte string2bits(String s, int nbits) {
		int pow = (int)Math.pow(2, nbits-1);
		int value = 0;
		do {
			if (s.substring(0,1).equals("1")) value += pow;
			pow = pow / 2;
			s = s.substring(1);
		} while (pow>0 && s.length()>0);
		return (byte)(value & 0xFF);
	}

	public static byte int2byte(int symbol) {
		return (byte)(symbol & 0xFF);
	}
	
	public static byte[] join(byte arrays[][]) {
		int totalSize = 0;
		for(int i=0; i<arrays.length; i++) {
			totalSize += arrays[i].length;
		}
		
		byte joined[] = new byte[totalSize];
		int index = 0;
		for(int i=0; i<arrays.length; i++) {
			for(int j=0; j<arrays[i].length; j++) {
				joined[index++] = arrays[i][j];
			}
		}
		return joined;
	}

	public static int hex2int(char c) {
		if (c>='0' && c<='9') {
			return c - '0';
		} else if (c>='A' && c<='F') {
			return c - 'A' + 10;
		} else if (c>='a' && c<='f') {
			return c - 'a' + 10;
		}
		return 0;
	}
}
