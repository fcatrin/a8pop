package fcatrin.pop;

public class Utils {
	public static int word(byte l, byte h) {
		return b2i(l) + 256 * b2i(h);
	}
	
	public static int b2i(byte b) {
		if (b>=0) return b;
		return b+256;
	}
}
