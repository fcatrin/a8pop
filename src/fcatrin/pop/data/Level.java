package fcatrin.pop.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import fcatrin.pop.Image;
import fcatrin.pop.views.ScreenView;

public class Level {
	private static final int ROWS = 3;
	private static final int TILES_PER_ROW = 10;
	private static final int TILES_PER_SCREEN = ROWS*TILES_PER_ROW;
	private static final int SCREENS = 24;
	private static final int DESCRIPTORS = TILES_PER_SCREEN * SCREENS;
	private static final int TILE_HEIGHT = 63;
	private static final int TILE_WIDTH = 16;
	byte type[] = new byte[DESCRIPTORS];
	byte spec[] = new byte[DESCRIPTORS];
	
	static Map<Integer, Image> tiles = new HashMap<Integer, Image>(); // TODO use an array when all tiles got created
	
	// object descriptors 
	// https://github.com/jmechner/Prince-of-Persia-Apple-II/blob/master/01%20POP%20Source/Source/BGDATA.S
	
	int maska[] = {00, 0x03, 0x03, 0x03, 0x03, 0x03, 0x03, 0x03, 0x03, 0x00, 0x03, 0x03, 0x00, 0x03, 0x03, 0x03, 
			0x03, 0x00, 0x00, 0x03, 0x00, 0x03, 0x00, 0x03, 0x00, 0x03, 0x00, 0x00, 0x00, 0x00};
	int piecea[] = {00, 0x01, 0x05, 0x07, 0x0a, 0x01, 0x01, 0x0a, 0x10, 0x00, 0x01, 0x00, 0x00, 0x14, 0x20, 0x4b,
			0x01, 0x00, 0x00, 0x01, 0x00, 0x97, 0x00, 0x01, 0x00, 0xa7, 0xa9, 0xaa, 0xac, 0xad};
	int pieceay[] = {00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, -4, -4, -4};
	int maskb[] = {00, 0x04, 0x04, 0x04, 0x04, 0x04, 0x04, 0x00, 0x04, 0x00, 0x04, 0x00, 0x00, 0x04, 0x04, 0x04,
			0x00, 0x04, 0x04, 0x04, 0x04, 0x04, 0x04, 0x00, 0x04, 0x04, 0x00, 0x00, 0x00, 0x00};
	int pieceb[] = {00, 0x02, 0x06, 0x08, 0x0b, 0x1b, 0x02, 0x9e, 0x1a, 0x1c, 0x02, 0x00, 0x9e, 0x4a, 0x21, 0x1b,
			0x4d, 0x4e, 0x02, 0x51, 0x84, 0x98, 0x02, 0x91, 0x92, 0x02, 0x00, 0x00, 0x00, 0x00};
	int pieceby[] = {00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0x03, 0x00, 0x03, 0x00, 0x00, 0x03, 0x00, 0x00, -1,
			0x00, 0x00, 0x00, -1, 0x02, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
	int bstripe[] = {00, 0x47, 0x47, 0x00, 0x00, 0x47, 0x47, 0x00, 0x00, 0x00, 0x47, 0x47, 0x00, 0x00, 0x47, 0x47,
			0x00, 0x00, 0x47, 0x00, 0x00, 0x00, 0x47, 0x00, 0x00, 0x47, 0x00, 0x00, 0x00, 0x00};
	int piecec[] = {00, 0x00, 0x00, 0x09, 0x0c, 0x00, 0x00, 0x9f, 0x00, 0x1d, 0x00, 0x00, 0x9f, 0x00, 0x00, 0x00,
			0x4f, 0x50, 0x00, 0x00, 0x85, 0x00, 0x00, 0x93, 0x94, 0x00, 0x00, 0x00, 0x00, 0x00};
	int pieced[] = {00, 0x15, 0x15, 0x15, 0x15, 0x18, 0x19, 0x16, 0x15, 0x00, 0x15, 0x00, 0x17, 0x15, 0x2e, 0x4c,
			0x15, 0x15, 0x15, 0x15, 0x86, 0x15, 0x15, 0x15, 0x15, 0x15, 0xab, 0x00, 0x00, 0x00};
	int fronti[] = {00, 0x00, 0x00, 0x45, 0x46, 0x00, 0x00, 0x46, 0x48, 0x49, 0x87, 0x00, 0x46, 0x0f, 0x13, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x83, 0x00, 0x00, 0x00, 0x00, 0xa8, 0x00, 0xae, 0xae, 0xae};
	int fronty[] = {00, 0x00, 0x00, -1, 0x00, 0x00, 0x00, 0x00, -1, 0x03, -3, 0x00, 0x00, -1, 0x00, 0x00, 
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, -1, 0x0, -36, -36, -36};
	int frontx[] = {00, 0x00, 0x00, 0x01, 0x03, 0x00, 0x00, 0x03, 0x01, 0x01, 0x02, 0x00, 0x03, 0x01, 0x00, 0x00, 
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00};
	
	public static void addTile(int position, File file) {
		try {
			tiles.put(position, Image.loadBMP(file));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
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
		byte objids[] = getScreen(screen);
		for(int row = 0; row<ROWS; row++) {
			String s = "";
			for(int i=0; i<TILES_PER_ROW; i++) {
				s+= String.format("%d, ", objids[row*TILES_PER_ROW+i]);
			}
			System.out.println(s);
		}
	}
	
	public void render(ScreenView screenView, int screen) {
		byte objids[] = getScreen(screen);
		for(int row = ROWS-1; row>=0; row--) {
			int top = row * TILE_HEIGHT;
			int bottom = top + TILE_HEIGHT;
			for(int i=0; i<TILES_PER_ROW; i++) {
				int left = i * TILE_WIDTH;
				int objid = objids[row*TILES_PER_ROW+i];
				
				if (i <TILES_PER_ROW-1 ) {
					int objidLeftBottom = (row<ROWS-1)?
							objids[(row+1)*TILES_PER_ROW+i+1] : 0;
							
					int objc = piecec[objidLeftBottom];
					if (objc!=0) {
						drawTileBaseBottom(screenView, bottom, left+TILE_WIDTH, objc);
					}
					int objb = pieceb[objid];
					drawTileBaseTop(screenView, top, left+TILE_WIDTH, objb);
				}
				
				int objd = pieced[objid];
				drawTileBaseBottom(screenView, bottom, left, objd);
				
				int obja = piecea[objid];
				drawTileBaseBottom(screenView, bottom, left, obja);
			
				int front = fronti[objid];
				drawTileBaseBottom(screenView, bottom, left, front);
			}
		}
	}

	private void drawTileBaseBottom(ScreenView screenView, int bottom, int left, int objid) {
		if (objid == 0) return;
		Image image = tiles.get(objid);
		if (image == null) return;
		
		int top = bottom - image.height;
		image.render(screenView, top, left);
	}
	
	
	private void drawTileBaseTop(ScreenView screenView, int top, int left, int objid) {
		if (objid == 0) return;
		Image image = tiles.get(objid);
		if (image == null) return;
		
		image.render(screenView, top, left);
	}
	
			
}
