package fcatrin.pop.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Level {
	private static final int ROWS = 3;
	private static final int TILES_PER_ROW = 10;
	private static final int TILES_PER_SCREEN = ROWS*TILES_PER_ROW;
	private static final int SCREENS = 24;
	private static final int DESCRIPTORS = TILES_PER_SCREEN * SCREENS;
	byte type[] = new byte[DESCRIPTORS];
	byte spec[] = new byte[DESCRIPTORS];
	
	public static Level load(File file) throws IOException {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			Level level = new Level();
			fis.read(level.type);
			return level;
		} finally {
			if (fis!=null) fis.close();
		}
	}
	
	public byte[] getScreen(int screen) {
		int base = screen * TILES_PER_SCREEN;
		byte objid[] = new byte[TILES_PER_SCREEN];
		for(int i=0; i<TILES_PER_SCREEN; i++) {
			objid[i] = (byte)(type[i+base] & 0x1F);
		}
		return objid;
	}
	
	public void debugScreen(int screen) {
		byte objid[] = getScreen(screen);
		for(int row = 0; row<ROWS; row++) {
			String s = "";
			for(int i=0; i<TILES_PER_ROW; i++) {
				s+= String.format("%d, ", objid[row*TILES_PER_ROW+i]);
			}
			System.out.println(s);
		}
	}
			
}
