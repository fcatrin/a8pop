package fcatrin.pop.compression;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import fcatrin.pop.utils.BitStream;
import fcatrin.pop.utils.Utils;

public class Dictionary {
	Map<String, Word> words = new HashMap<String, Word>();
	private ArrayList<Word> lstWords;
	private Set<Byte> freeCodes = new HashSet<Byte>();
	Map<String, Word> dictionary = new HashMap<String, Word>();
	
	class Word {
		String text;
		int times;
		String code;
	}
	
	public Dictionary() {
		for(byte i=Byte.MIN_VALUE; i<Byte.MAX_VALUE; i++) {
			System.out.println(i);
			freeCodes.add(i);
		}
	}
	
	// slowest method
	
	public void init(byte data[]) {
		for(int i=0; i<data.length; i++) {
			freeCodes.remove(data[i]);
		}
		
		for(int n=2; n<10; n++) {
			for(int i=0; i<data.length-n; i++) {
				String word = buildWord(data, i, n);
				//System.out.println("word: " + word);
				Word knownWord = words.get(word);
				if (knownWord == null) {
					knownWord = new Word();
					knownWord.text = word;
				}
				knownWord.times++;
				words.put(word, knownWord);
			}
		}
	}
	
	public void buildDictionary() {
		lstWords = new ArrayList<Word>();
		lstWords.addAll(words.values());
		
		Collections.sort(lstWords, new Comparator<Word>(){

			@Override
			public int compare(Word o1, Word o2) {
				int textCompare = (o1.text.length() == o2.text.length())?0:
					(o1.text.length()<o2.text.length()?1:-1);
				
				int timesCompare = (o1.times == o2.times)?0:(o1.times < o2.times?1:-1); 
				return textCompare==0?timesCompare:textCompare;
				//return timesCompare == 0?textCompare:timesCompare;
			}
		});
		for(Word word : lstWords) {
			System.out.println(word.text + ":" + word.times);
		}
	}
	
	int symbols = 0;
	private byte[] compress(byte data[]) {
		int skip = 0;
		System.out.println("origin:" + data.length);
		for(Word word : lstWords) {
			if (skip>0) {
				skip--;
				continue;
			}
			if (word.times<=1) continue;
			BitStream bs = new BitStream(data);
			String oldStream = bs.asString();
			String newStream = oldStream.replace(word.text, "..");
			if (!oldStream.equals(newStream)) {
				if (word.code==null) {
					word.code = getNextFreeCode();
					symbols++;
				}
				if (word.code!=null) {
					dictionary.put(word.text, word);
					newStream = newStream.replace("..", word.code);
				} else {
					System.out.println("No more free codes");
					newStream = oldStream;
				}
				
			}
			bs = new BitStream();
			bs.appendHex(newStream);
			data = bs.asBytes();
		}
		System.out.println("target:" + data.length + ", symbols:" + symbols);
		return data;
	}
	
	private String getNextFreeCode() {
		if (freeCodes.isEmpty()) return null;
		
		Byte code = freeCodes.iterator().next();
		freeCodes.remove(code);
		return String.format("%02x", code);
	}
	
	private String buildWord(byte data[], int offset, int len) {
		String s = "";
		for(int i=0; i<len; i++) {
			byte d = data[offset+i];
			s += String.format("%02x", Utils.b2i(d));
		}
		return s;
	}
	
	private static byte[] load(File file) throws IOException {
		/*
		 * 	byte linkMap[] = new byte[MAX_LINK];
	byte linkLoc[] = new byte[MAX_LINK];
	byte map[] = new byte[SCREENS*4];
	byte info[] = new byte[256];
		 */
		byte type[] = new byte[24*30];
		byte spec[] = new byte[24*30];
		byte linkMap[] = new byte[256];
		byte linkLoc[] = new byte[256];
		byte map[] = new byte[24*4];
		byte info[] = new byte[256];
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			fis.read(type);
			fis.read(spec);
			fis.read(linkMap);
			fis.read(linkLoc);
			fis.read(map);
			fis.read(info);
			return type;
		} finally {
			if (fis!=null) fis.close();
		}
	}
	
	public static void main(String args[]) throws Exception {
		Dictionary dict = new Dictionary();
		byte data[][] = new byte[15][];
		for(int i=0; i<15; i++) {
			byte datal[] = load(new File("levels/LEVEL" + i));
			dict.init(datal);
			data[i] = datal;
		}
		dict.buildDictionary();
		
		byte dataDictCompressed[][] = new byte[15][];
		for(int i=0; i<15; i++) {
			System.out.println("dict compress " + i);
			dataDictCompressed[i] = dict.compress(data[i]);
		}

		byte dataDictCompressedAll[] = Utils.join(dataDictCompressed);
		
		Huffman h = new Huffman();
		byte[] compressHuffman = h.compress(dataDictCompressedAll);
		System.out.println("initial size:" + (720*15));
		System.out.println("dict: " + dataDictCompressedAll.length);
		System.out.println("huffman: " + compressHuffman.length);
	}
	
}
