package fcatrin.pop.compression;

import java.util.Arrays;

import fcatrin.pop.utils.Utils;

public class LZ {
	private static int WINDOW = 8;
	
	public static class LZData {
		public byte compressed[];
		public byte literalFlags[];
		public byte literals[];
	}
	
	public static LZData compress(byte data[]) {
		int offsets[] = new int[256];
		int length[] = new int[256];
		
		int compressedIndex = 0;
		int compressedBlock = 0;
		int compressedLiteralIndex = 0;
		byte compressed[] = new byte[data.length];
		byte literalFlags[] = new byte[data.length];
		byte literals[] = new byte[data.length];
		
		int src = 0;
		while (src<data.length) {
			int lookBack = src - WINDOW;
			if (lookBack<0) lookBack = 0;
			
			int matchFound = 0;
			while (lookBack<src) {
				int lookBackSave = lookBack;
				int currentString = src;
				int currentSize = 0;
				
				while (lookBack<src && currentString<data.length && currentSize<8) {
					//System.out.println("lookBack " + lookBack + ", currentString:" + currentString + ", src:" + src);
					if (data[lookBack] == data[currentString]) {
						currentSize++;
						currentString++;
						lookBack++;
					} else {
						break;
					}
				};
				if (currentSize>1) {
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
				literalFlags[compressedBlock++] = 0;
				compressed[compressedIndex++] = Utils.int2byte(src-longestOffset);
				compressed[compressedIndex++] = Utils.int2byte(longestSize);
				String format = "add zip[%d] offset:%d size:%d, cblock[%d] = false";
				System.out.println(String.format(format, compressedIndex-2, longestOffset, longestSize, compressedBlock-1));
				src+=longestSize;
			} else {
				literalFlags[compressedBlock++] = 1;
				literals[compressedLiteralIndex++] = data[src];
				String format = "add literal[%d]=%d, cblock[%d] = true";
				System.out.println(String.format(format, compressedIndex-1, data[src], compressedBlock-1));
				src++;
			}
		}
		
		String format = "lz src:%d, zip:%d, extra:%d";
		System.out.println(String.format(format, data.length, compressedIndex, (compressedBlock+7)/8));
		
		LZData lz = new LZData();
		lz.compressed = Arrays.copyOf(compressed, compressedIndex);
		lz.literalFlags = Arrays.copyOf(literalFlags, compressedBlock);
		lz.literals = Arrays.copyOf(literals, compressedLiteralIndex);
		return lz;
		
	}
}
