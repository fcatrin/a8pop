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
	
	// special cases
	private static final int OBJID_SPACE = 0;
	private static final int OBJID_BLOCK = 20;
	private static final int OBJID_LOOSE = 11;
	private static final int TILE_BLOCK_RIGHT = 0xA0;
	private static final int TILE_BLOCK_LEFT = 0xA1;
	private static final int TILE_BLOCK_D_RIGHT = 0xA2;
	private static final int TILE_BLOCK_D_LEFT = 0xA3;
	
	private static final int MAX_TROBS = 0x20;
	private final int trLoc[]       = new int[MAX_TROBS];
	private final int trScreen[]    = new int[MAX_TROBS];
	private final int trDirection[] = new int[MAX_TROBS];
	private final int trObjid[] = new int[MAX_TROBS];
	private int trobs = 0;
	
	
	static Map<Integer, Image> tiles = new HashMap<Integer, Image>(); // TODO use an array when all tiles got created
	
	// object descriptors 
	// https://github.com/jmechner/Prince-of-Persia-Apple-II/blob/master/01%20POP%20Source/Source/BGDATA.S
	
	/*
	 * 
	 *	 0  1  2  3  4  5  6  7  8  9 10 11 12 13 14 15
	 *	16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31
	 */
	
	int maska[] = {00, 0x03, 0x03, 0xa4, 0x03, 0x03, 0x03, 0x03, 0x03, 0x00, 0x03, 0x03, 0x00, 0x03, 0x03, 0xa6, 
			0x03, 0x00, 0x00, 0x03, 0x00, 0x03, 0x00, 0x03, 0x00, 0x03, 0x00, 0x00, 0x00, 0x00};
	int piecea[] = {00, 0x01, 0x05, 0x07, 0x0a, 0x01, 0x01, 0x0a, 0x10, 0x00, 0x01, 0x01, 0x00, 0x14, 0x20, 0x4b,
			0x01, 0x00, 0x00, 0x01, 0x00, 0x97, 0x00, 0x01, 0x00, 0xa7, 0xa9, 0xaa, 0xac, 0xad};
	int pieceay[] = {00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, -4, -4, -4};
	int maskb[] = {00, 0x04, 0x04, 0x04, 0x04, 0x04, 0x04, 0x00, 0x04, 0x00, 0x04, 0x04, 0x00, 0x04, 0x04, 0x04,
			0x00, 0x04, 0x04, 0x04, 0x04, 0x04, 0x04, 0x00, 0x04, 0x04, 0x00, 0x00, 0x00, 0x00};
	int pieceb[] = {00, 0x02, 0x06, 0x08, 0x0b, 0x1b, 0x02, 0x9e, 0x1a, 0x1c, 0x02, 0x02, 0x9e, 0x4a, 0x21, 0x1b,
			0x4d, 0x4e, 0x02, 0x51, 0x84, 0x98, 0x02, 0x91, 0x92, 0x02, 0x00, 0x00, 0x00, 0x00};
	int pieceby[] = {00, 0, 0x00, 0x00, 0x00, 0x01, 0x00, 0x03, 0x00, 0x03, 0x00, 0x00, 0x03, 0x00, 0x00, -1,
			0x00, 0x00, 0x00, 0, 0x0, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
	int bstripe[] = {00, 0x47, 0x47, 0x00, 0x00, 0x47, 0x47, 0x00, 0x00, 0x00, 0x47, 0x47, 0x00, 0x00, 0x47, 0x47,
			0x00, 0x00, 0x47, 0x00, 0x00, 0x00, 0x47, 0x00, 0x00, 0x47, 0x00, 0x00, 0x00, 0x00};
	int piecec[] = {00, 0x00, 0x00, 0x09, 0x0c, 0x00, 0x00, 0x9f, 0x00, 0x1d, 0x00, 0x00, 0x9f, 0x00, 0x00, 0x00,
			0x4f, 0x50, 0x00, 0x00, 0x85, 0x00, 0x00, 0x93, 0x94, 0x00, 0x00, 0x00, 0x00, 0x00};
	int pieced[] = {00, 0x15, 0x15, 0x15, 0x15, 0x18, 0x19, 0x16, 0x15, 0x00, 0x15, 0x15, 0x17, 0x15, 0x15, 0x4c,
			0x15, 0x15, 0x15, 0x15, 0x86, 0x15, 0x15, 0x15, 0x15, 0x15, 0xab, 0x00, 0x00, 0x00};
	int fronti[] = {00, 0x00, 0x00, 0x45, 0x46, 0x00, 0x00, 0x46, 0x48, 0x49, 0x87, 0x00, 0x46, 0x0f, 0x13, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x83, 0x00, 0x00, 0x00, 0x00, 0xa8, 0x00, 0xae, 0xae, 0xae};
	int fronty[] = {00, 0x00, 0x00, -3, -3, 0x00, 0x00, 0x00, -1, 0x03, -6, 0x00, 0x00, -1, -3, 0x00, 
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, -1, 0x0, -36, -36, -36};
	int frontx[] = {00, 0x00, 0x00, 0x00, 0x0C, 0x00, 0x00, 0x03, 0x01, 0x01, 0x0A, 0x00, 0x03, 0x01, 0x00, 0x00, 
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00};
	
	// 0x00 = autoMask
	// 0xFF = solid
	int maskFront[] = {00, 0x00, 0x00, 0xa4, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0xa5, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0xff, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
	
	static final int looseFrames = 11; 
	static final int looseb = 0x1b;
	int looseby[] = {00,01,00,-1,-1,00,00,00,-1,-1,-1};
	int loosea[] = {0x01,0x1e,0x01,0x1f,0x1f,0x01,0x01,0x01,0x1f,0x1f,0x1f};
	int loosed[] = {0x15,0x2c,0x15,0x2d,0x2d,0x15,0x15,0x15,0x2d,0x2d,0x2d};
	
	// TODO verificar si rubble requiere pieza frontal: Tile 0x13. Por ahora se incluye
	// TODO pieza rubble D se reemplazo por la de piso normal
	// TODO tile 0x19 es igual al 0x4c (unpressed floor)
	// TODO se cambian piezas para que objeto 11 (loose floor) quede normal al principio
	
	public boolean drawA = true;
	public boolean drawB = true;
	public boolean drawC = true;
	public boolean drawD = true;
	public boolean drawF = true;	
	
	public Level() {
	}
	
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
	
	public int getTROB(int screen, int position) {
		for(int i=0; i<trobs; i++) {
			int trobScreen = trScreen[i];
			int trobLoc = trLoc[i];
			if (trobScreen == screen && trobLoc == position) return i;
		}
		return -1;
	}
	
	public int addTROB(int screen, int position, int objid) {
		int trob = getTROB(screen, position);
		if (trob<0) {
			if (trobs == MAX_TROBS) return -1; // should fail
			trob = trobs;
			trScreen[trob] = screen;
			trLoc[trob] = position;
			trDirection[trob] = 0;
			trObjid[trob] = objid;
			trobs++;
		}
		return trob;
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
	
	public boolean advanceFrame() {
		for(int i=0; i<trobs; i++) {
			int direction = trDirection[i];
			if (direction == -1) continue;
			
			int screen = trScreen[i];
			int position = trLoc[i];
			int linearLoc = screen * TILES_PER_SCREEN + position;
			int obj = type[linearLoc];
			int objid = obj & 0x1F;
			if (objid == OBJID_LOOSE && (obj & 0xE0) != 0) { // hight bits indicates animation
				int times = (direction & 0xF0) >> 4;
				int frame = (direction & 0x0F) + 1;
				if (frame == looseFrames) {
					if (times == 2) { // disappear on the second time. Just for testing
						trDirection[i] = -1;
						type[linearLoc] = OBJID_SPACE;
					} else {
						times++;
						frame = 0;
						type[linearLoc] = OBJID_LOOSE; // stop animation
					}
				}
				trDirection[i] = (times << 4) | frame;
				return true;
			}
		}
		return false;
	}
	
	public void moveFloor(int screen) {
		int base = screen * TILES_PER_SCREEN;
		for(int i=0; i<TILES_PER_SCREEN; i++) {
			if (type[base+i] == OBJID_LOOSE) {
				type[base+i] = OBJID_LOOSE | 0x20; // turn on animation flag;
				return;
			}
		}
	}
	
	int currentObjId = 0;
	public void render(ScreenView screenView, int screen) {
		byte objids[] = getScreen(screen);
		for(int row = ROWS-1; row>=0; row--) {
			int top = row * TILE_HEIGHT;
			int bottom = top + TILE_HEIGHT;
			for(int i=0; i<TILES_PER_ROW; i++) {
				int linearPos = row*TILES_PER_ROW+i;
				int left = i * TILE_WIDTH;
				int objid = objids[linearPos];
				
				int objidLeftBottom = (row<ROWS-1)?
						objids[linearPos + TILES_PER_ROW] : 0;
						
				int objA = piecea[objid];
				int objB = 0;
				int objC = 0;
				int objD = pieced[objid];
				int objF = fronti[objid];
				int objAmask = maska[objid];
				int objBmask = 0;
				int objFmask = maskFront[objid];
				int objAy = pieceay[objid];
				int objBy = 0;
				int objFy = fronty[objid];
				int objFx = frontx[objid];

				
				if (i<TILES_PER_ROW-1) {
					currentObjId = objidLeftBottom;
					objC = piecec[objidLeftBottom];
					
					currentObjId = objid;
					objB = pieceb[objid];
					objBy = pieceby[objid];
					objBmask = maskb[objid];
				}
				currentObjId = objid;
				
				if (objid == OBJID_BLOCK) { 
					if (i<TILES_PER_ROW-1) {
						int objidRight = objids[linearPos+1];
						if (objidRight!=OBJID_BLOCK) {
							objF = TILE_BLOCK_RIGHT;
							objD = TILE_BLOCK_D_RIGHT;
						}
					}
					if (i>0) {
						int objidLeft = objids[linearPos-1];
						if (objidLeft!=OBJID_BLOCK) {
							objD = TILE_BLOCK_LEFT;
							objF = TILE_BLOCK_D_LEFT;
						}
					}
				} else if (objid == OBJID_LOOSE) {
					int trob = addTROB(screen, linearPos, objid);
					int direction = trDirection[trob];
					if (direction > 0 ) {  // moving
						direction &= 0x0f;
						objB = looseb;
						objA = loosea[direction];
						objBy = looseby[direction];
						objD = loosed[direction];
					}
				}


				int nextLeft = left + TILE_WIDTH;
				if (drawC) drawTileBaseBottom(screenView, bottom, nextLeft, objC);
				if (drawB) drawTileBaseBottom(screenView, bottom + objBy, nextLeft, objB, objBmask, false);
				if (drawD) drawTileBaseBottom(screenView, bottom, left, objD);
				if (drawA) drawTileBaseBottom(screenView, bottom-3 + objAy, left, objA, objAmask, false);
				if (drawF) drawTileBaseBottom(screenView, bottom + objFy, left + objFx, objF, objFmask, objFmask==0);
			}
		}
	}

	private void drawTileBaseBottom(ScreenView screenView, int bottom, int left, int tileId) {
		drawTileBaseBottom(screenView, bottom, left, tileId, 0, false);
	}

	private void drawTileBaseBottom(ScreenView screenView, int bottom, int left, int tileId, boolean autoMask) {
		drawTileBaseBottom(screenView, bottom, left, tileId, 0, autoMask);
	}
	
	private void drawTileBaseBottom(ScreenView screenView, int bottom, int left, int tileId, int tileMaskId, boolean autoMask) {
		Image image = tiles.get(tileId);
		if (image == null) {
			if (tileId!=0) System.out.println(String.format("Tile %d not found for object id %d", tileId, currentObjId));
			return;
		}
		
		Image mask = tiles.get(tileMaskId);
		if (mask == null) {
			if (tileMaskId!=0 && tileMaskId!=0xff) System.out.println(String.format("Mask %d not found for object id %d", tileMaskId, currentObjId));
		}
		
		image.renderBottom(screenView, bottom, left, mask, autoMask);
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
