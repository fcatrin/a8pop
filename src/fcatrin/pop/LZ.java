package fcatrin.pop;

import java.util.Arrays;

public class LZ {
	private static int WINDOW = 256;
	
	public static class LZData {
		public int compressed[];
		public boolean literal[];
	}
	
	public static LZData compress(int data[]) {
		int offsets[] = new int[256];
		int length[] = new int[256];
		
		int compressedIndex = 0;
		int compressedBlock = 0;
		int compressed[] = new int[data.length];
		boolean literal[] = new boolean[data.length];
		
		int src = 0;
		while (src<data.length) {
			int lookBack = src - WINDOW;
			if (lookBack<0) lookBack = 0;
			
			int matchFound = 0;
			while (lookBack<src) {
				int lookBackSave = lookBack;
				int currentString = src;
				int currentSize = 0;
				
				while (lookBack<src && currentString<data.length) {
					//System.out.println("lookBack " + lookBack + ", currentString:" + currentString + ", src:" + src);
					if (data[lookBack] == data[currentString]) {
						currentSize++;
						currentString++;
						lookBack++;
					} else {
						break;
					}
				};
				if (currentSize>2) {
					offsets[matchFound] = lookBackSave;
					length [matchFound] = currentSize;
					matchFound++;
				}
				lookBack = lookBackSave+1;
			}
			if (matchFound>0) {
				int longestSize = 0;
				int longestOffset = 0;
				for(int i=0; i<matchFound; i++) {
					if (length[i]>longestSize) {
						longestSize = length[i];
						longestOffset = offsets[i];
					}
				}
				literal[compressedBlock++] = false;
				compressed[compressedIndex++] = src-longestOffset;
				compressed[compressedIndex++] = longestSize;
				String format = "add zip[%d] offset:%d size:%d, cblock[%d] = false";
				System.out.println(String.format(format, compressedIndex-2, longestOffset, longestSize, compressedBlock-1));
				src+=longestSize;
			} else {
				literal[compressedBlock++] = true;
				compressed[compressedIndex++] = data[src];
				String format = "add literal[%d]=%d, cblock[%d] = true";
				System.out.println(String.format(format, compressedIndex-1, data[src], compressedBlock-1));
				src++;
			}
		}
		
		String format = "lz src:%d, zip:%d, extra:%d";
		System.out.println(String.format(format, data.length, compressedIndex, (compressedBlock+7)/8));
		
		LZData lz = new LZData();
		lz.compressed = Arrays.copyOf(compressed, compressedIndex);
		lz.literal = Arrays.copyOf(literal, compressedBlock);
		return lz;
		
	}
}
