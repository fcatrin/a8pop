package fcatrin.pop.compression;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import fcatrin.pop.utils.BitStream;
import fcatrin.pop.utils.Utils;

public class Dictionary {
	Map<String, Word> words = new HashMap<String, Word>();
	private ArrayList<Word> lstWords;
	
	class Word {
		String text;
		int times;
	}
	
	// slowest method
	
	public void init(byte data[]) {
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
				
				return (o1.times == o2.times)? 
					textCompare:
					(o1.times < o2.times?1:-1);
			}
		});
		for(Word word : lstWords) {
			System.out.println(word.text + ":" + word.times);
		}
	}
	
	private byte[] compress(byte data[]) {
		int skip = 0;
		int symbols = 0;
		System.out.println("origin:" + data.length);
		for(Word word : lstWords) {
			if (skip>0) {
				skip--;
				continue;
			}
			if (word.times<=1) continue;
			BitStream bs = new BitStream(data);
			String oldStream = bs.asString();
			String newStream = oldStream.replace(word.text, "FF");
			if (!oldStream.equals(newStream)) symbols++;
			bs = new BitStream();
			bs.appendHex(newStream);
			data = bs.asBytes();
		}
		System.out.println("target:" + data.length + ", symbols:" + symbols);
		return data;
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
		BitStream bs = new BitStream();
		bs.append(dict.compress(data[1]));
		System.out.println(bs.asString());
	}
	
}
