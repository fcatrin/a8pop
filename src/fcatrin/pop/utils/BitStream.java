package fcatrin.pop.utils;

public class BitStream {
	StringBuffer data = new StringBuffer();
	
	public BitStream() {
		
	}
	
	public BitStream(byte[] data) {
		for(byte b : data) {
			append(Utils.b2i(b), 8);
		}
	}
	
	public void append(int value, int nbits) {
		data.append(Utils.bits2string(value, nbits));
	}
	
	public void append(String sBits) {
		data.append(sBits);
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
	
	public static void main(String args[]) throws Exception {
		BitStream bs = new BitStream();
		bs.append(255, 8);
		bs.append(240, 8);
		bs.append(15, 8);
		bs.append(0x55, 8);
		bs.append(0xAA, 8);
		bs.append(0x05, 3);
		System.out.println(bs.dump());
		
		byte[] bytes = bs.asBytes();
		BitStream bs2 = new BitStream(bytes);
		System.out.println(bs2.dump());
	}

}
