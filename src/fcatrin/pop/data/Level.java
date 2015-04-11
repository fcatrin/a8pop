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
	
	private static final int OBJID_BLOCK = 20;
	private static final int TILE_BLOCK_RIGHT = 0xA0;
	
	static Map<Integer, Image> tiles = new HashMap<Integer, Image>(); // TODO use an array when all tiles got created
	
	// object descriptors 
	// https://github.com/jmechner/Prince-of-Persia-Apple-II/blob/master/01%20POP%20Source/Source/BGDATA.S
	
	/*
	 * 
	 *	 0  1  2  3  4  5  6  7  8  9 10 11 12 13 14 15
	 *	16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31
	 */
	
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
	int pieceby[] = {00, -3, 0x00, 0x00, 0x00, 0x01, 0x00, 0x03, 0x00, 0x03, 0x00, 0x00, 0x03, 0x00, 0x00, -1,
			0x00, 0x00, 0x00, -3, 0x0, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
	int bstripe[] = {00, 0x47, 0x47, 0x00, 0x00, 0x47, 0x47, 0x00, 0x00, 0x00, 0x47, 0x47, 0x00, 0x00, 0x47, 0x47,
			0x00, 0x00, 0x47, 0x00, 0x00, 0x00, 0x47, 0x00, 0x00, 0x47, 0x00, 0x00, 0x00, 0x00};
	int piecec[] = {00, 0x00, 0x00, 0x09, 0x0c, 0x00, 0x00, 0x9f, 0x00, 0x1d, 0x00, 0x00, 0x9f, 0x00, 0x00, 0x00,
			0x4f, 0x50, 0x00, 0x00, 0x85, 0x00, 0x00, 0x93, 0x94, 0x00, 0x00, 0x00, 0x00, 0x00};
	int pieced[] = {00, 0x15, 0x15, 0x15, 0x15, 0x18, 0x19, 0x16, 0x15, 0x00, 0x15, 0x00, 0x17, 0x15, 0x2e, 0x4c,
			0x15, 0x15, 0x15, 0x15, 0x86, 0x15, 0x15, 0x15, 0x15, 0x15, 0xab, 0x00, 0x00, 0x00};
	int fronti[] = {00, 0x00, 0x00, 0x45, 0x46, 0x00, 0x00, 0x46, 0x48, 0x49, 0x87, 0x00, 0x46, 0x0f, 0x13, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x83, 0x00, 0x00, 0x00, 0x00, 0xa8, 0x00, 0xae, 0xae, 0xae};
	int fronty[] = {00, 0x00, 0x00, -3, 0x00, 0x00, 0x00, 0x00, -1, 0x03, -3, 0x00, 0x00, -1, 0x00, 0x00, 
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, -1, 0x0, -36, -36, -36};
	int frontx[] = {00, 0x00, 0x00, 0x04, 0x03, 0x00, 0x00, 0x03, 0x01, 0x01, 0x02, 0x00, 0x03, 0x01, 0x00, 0x00, 
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
				
				int objidLeftBottom = (row<ROWS-1)?
						objids[(row+1)*TILES_PER_ROW+i] : 0;
				
				if (i<TILES_PER_ROW-1) {
					int nextLeft = left + TILE_WIDTH;
					int objc = piecec[objidLeftBottom];
					if (objc!=0) {
						drawTileBaseBottom(screenView, bottom, nextLeft, objc);
					}
					int objb = pieceb[objid];
					int objby = pieceby[objid];
					int objmaskb = maskb[objid];
					System.out.println(String.format("objid:%d, b:%d", objid, objb));
					drawTileBaseBottom(screenView, bottom + objby, nextLeft, objb, objmaskb);
				}
				
				int objd = pieced[objid];
				drawTileBaseBottom(screenView, bottom, left, objd);
				
				int objmaska = maska[objid];
				int obja = piecea[objid];
				drawTileBaseBottom(screenView, bottom-3, left, obja, objmaska);
			
				int front = fronti[objid];
				if (i<TILES_PER_ROW-1) {
					int objidRight = objids[row*TILES_PER_ROW+i+1];
					if (objid == OBJID_BLOCK && objidRight!=OBJID_BLOCK) {
						front = TILE_BLOCK_RIGHT;
					}
				}
				int frontDy = fronty[objid];
				int frontDx = frontx[objid];
				drawTileBaseBottom(screenView, bottom + frontDy, left + frontDx, front);
			}
		}
	}

	private void drawTileBaseBottom(ScreenView screenView, int bottom, int left, int objid) {
		drawTileBaseBottom(screenView, bottom, left, objid, 0);
	}
	
	private void drawTileBaseBottom(ScreenView screenView, int bottom, int left, int objid, int objmaskb) {
		Image image = tiles.get(objid);
		if (image == null) return;
		
		Image mask = tiles.get(objmaskb);
		
		image.renderBottom(screenView, bottom, left, mask);
	}
	
	
	private void drawTileBaseTop(ScreenView screenView, int top, int left, int objid) {
		Image image = tiles.get(objid);
		if (image == null) return;
		
		image.render(screenView, top, left);
	}
	
	
	/*
	 * *-------------------------------
*
*  Return cs if C-section is visible, cc if hidden
*
*-------------------------------
checkc
 lda objid ;Does this space contain solid floorpiece?
 beq :vis
 cmp #pillartop
 beq :vis
 cmp #panelwof
 beq :vis
 cmp #archtop1
 bcs :vis
 bcc ]rts ;C-section is hidden
:vis sec ;C-section is visible
]rts rts
*/
	 
	
	/*
	 * *-------------------------------
*
*  Redraw entire block
*
*-------------------------------
RedBlockSure
 jsr drawc ;C-section of piece below & to left
 jsr drawmc

 jsr drawb ;B-section of piece to left
 jsr drawmb

 jsr drawd ;D-section
 jsr drawmd

 jsr drawa ;A-section
 jsr drawma

 jmp drawfrnt ;A-section frontpiece
;(Note: This is necessary in case we do a
;layersave before we get to f.g. plane)
	 */
			
}
