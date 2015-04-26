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
}
