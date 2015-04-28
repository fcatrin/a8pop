package fcatrin.pop.compression;

import java.util.ArrayList;
import java.util.List;

public class RLE {
	
	boolean literal[]; 
	byte values[];
	byte compressed[];
	byte sizes[];
	
	public void compress(byte data[]) {
		List<Boolean> lstLiteral = new ArrayList<Boolean>();
		List<Byte> lstValues = new ArrayList<Byte>();
		List<Byte> lstCompress = new ArrayList<Byte>();
		List<Byte> lstSizes = new ArrayList<Byte>();
		int i=0;
		while(i<data.length) {
			byte value = data[i];
			int j=i+1;
			byte n=0;
			while (j<data.length & n<256) {
				if (value != data[j]) break;
				j++;
				n++;
			}
			i=j;
			lstLiteral.add(n==0);
			lstValues.add(value);
			lstCompress.add(value);
			if (n>0) {
				lstCompress.add(n);
			}
			lstSizes.add(n);
		}
		literal = new boolean[lstLiteral.size()];
		values = new byte[lstValues.size()];
		compressed = new byte[lstCompress.size()];
		sizes = new byte[lstSizes.size()];

		
		for(i=0; i<lstLiteral.size(); i++) {
			literal[i] = lstLiteral.get(i);
		}
		for(i=0; i<lstValues.size(); i++) {
			values[i] = lstValues.get(i);
		}
		for(i=0; i<lstCompress.size(); i++) {
			compressed[i] = lstCompress.get(i);
		}
		for(i=0; i<lstSizes.size(); i++) {
			sizes[i] = lstSizes.get(i);
		}
	}
}
