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
	private static final int MAX_LINK = 256;
	byte type[] = new byte[DESCRIPTORS];
	byte spec[] = new byte[DESCRIPTORS]; // TODO process patterns, state
	byte linkMap[] = new byte[MAX_LINK];
	byte linkLoc[] = new byte[MAX_LINK];
	byte map[] = new byte[SCREENS*4];
	byte info[] = new byte[256];
	
	private static final int MAP_LEFT = 0;
	private static final int MAP_RIGHT = 1;
	private static final int MAP_TOP = 2;
	private static final int MAP_BOTTOM = 3;
	
	// special cases
	private static final int OBJID_SPACE = 0;
	private static final int OBJID_BLOCK = 20;
	private static final int OBJID_LOOSE = 11;
	private static final int OBJID_GATE_RIGHT = 17;
	
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
			0x03, 0x00, 0x00, 0x03, 0x00, 0xa7, 0x00, 0x03, 0x00, 0x03, 0x00, 0x00, 0x00, 0x00};
	int piecea[] = {00, 0x01, 0x05, 0x07, 0x0a, 0x01, 0x01, 0x0a, 0x10, 0x00, 0x01, 0x01, 0x00, 0x14, 0x20, 0x4b,
			0x01, 0x00, 0x00, 0x01, 0x00, 0x97, 0x00, 0x01, 0x00, 0xa7, 0xa9, 0xaa, 0xac, 0xad};
	int pieceay[] = {00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, -4, -4, -4};
	int maskb[] = {00, 0x04, 0x04, 0x04, 0x04, 0x04, 0x04, 0x00, 0x04, 0x00, 0x04, 0x04, 0x00, 0x04, 0x04, 0x04,
			0x00, 0x04, 0x04, 0x04, 0x04, 0x04, 0x04, 0x00, 0x04, 0x04, 0x00, 0x00, 0x00, 0x00};
	int pieceb[] = {00, 0x02, 0x06, 0x08, 0x0b, 0x1b, 0x02, 0x9e, 0x1a, 0x1c, 0x02, 0x02, 0x9e, 0x4a, 0x21, 0x1b,
			0x4d, 0x4e, 0x02, 0x51, 0x84, 0x98, 0x02, 0x91, 0x92, 0x02, 0x00, 0x00, 0x00, 0x00};
	int pieceby[] = {00, 0, 0x00, 0x00, 0x00, 0x01, 0x00, 0x03, 0x00, 0x03, 0x00, 0x00, 0x03, 0x00, 0x00, -1,
			-3, 0, 0x00, 0, 0x0, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
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
	
	private static final int DOOR_HEIGHT = 52;
	private static final int DOOR_LINE_HEIGHT = 8;
	
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
	
	int currentScreen = -1;
	
	boolean doorOpened = false;
	boolean doorOpening = false;
	int doorPosition = 0;
	
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
			fis.read(level.spec);
			fis.read(level.linkLoc);
			fis.read(level.linkMap);
			fis.read(level.map);
			fis.read(level.info);
			level.setCurrentScreen(0);
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
		boolean changed = false;
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
				changed = true;
				break;
			}
		}
		
		if (doorOpening) {
			doorPosition++;
			if (doorPosition>DOOR_HEIGHT) {
				doorOpening = false;
				doorOpened = true;
			}
			changed = true;
		}
		return changed;
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
	
	public void openDoor() {
		if (!doorOpened) doorOpening = true;
	}
	
	int currentObjId = 0;
	public void render(ScreenView screenView) {
		for(int i=0; drawC && i<drawBlocksC.length; i++) {
			DrawBlock drawBlock = drawBlocksC[i];
			if (drawBlock.piece != 0) {
				drawTileBaseBottom(screenView, drawBlock.bottom, drawBlock.left, drawBlock.piece);
			}
		}
		for(int i=0; drawB && i<drawBlocksB.length; i++) {
			DrawBlock drawBlock = drawBlocksB[i];
			if (drawBlock.piece != 0) {
				//System.out.println("index: " + i + ", bottom:" + drawBlock.bottom + ", left:" + drawBlock.left);
				drawTileBaseBottom(screenView, drawBlock.bottom, drawBlock.left, drawBlock.piece, drawBlock.mask, false);
			}
		}
		for(int i=0; drawD && i<drawBlocksD.length; i++) {
			DrawBlock drawBlock = drawBlocksD[i];
			if (drawBlock.piece != 0) {
				drawTileBaseBottom(screenView, drawBlock.bottom, drawBlock.left, drawBlock.piece,drawBlock.mask, false);
			}
		}
		for(int i=0; drawA && i<drawBlocksA.length; i++) {
			DrawBlock drawBlock = drawBlocksA[i];
			if (drawBlock.piece != 0) {
				drawTileBaseBottom(screenView, drawBlock.bottom, drawBlock.left, drawBlock.piece, drawBlock.mask, false);
			}
		}
		
		for(int i=0; i<drawBlocksExtraBackground.length; i++) {
			DrawBlock drawBlock = drawBlocksExtraBackground[i];
			if (drawBlock!=null && drawBlock.piece != 0) {
				drawTileBaseBottom(screenView, drawBlock.bottom, drawBlock.left, drawBlock.piece, 0, false, drawBlock.height);
			}
		}
		for(int i=0; drawF && i<drawBlocksF.length; i++) {
			DrawBlock drawBlock = drawBlocksF[i];
			if (drawBlock.piece != 0) {
				drawTileBaseBottom(screenView, drawBlock.bottom, drawBlock.left, drawBlock.piece, drawBlock.mask, drawBlock.mask==0);
			}
		}
	}
	
	class DrawBlock {
		int piece;
		int mask;
		int bottom;
		int left;
		int height;
	}
	
	private DrawBlock drawBlocksC[] = new DrawBlock[TILES_PER_SCREEN];
	private DrawBlock drawBlocksB[] = new DrawBlock[TILES_PER_SCREEN];
	private DrawBlock drawBlocksD[] = new DrawBlock[TILES_PER_SCREEN + TILES_PER_ROW];
	private DrawBlock drawBlocksA[] = new DrawBlock[TILES_PER_SCREEN];
	private DrawBlock drawBlocksExtraBackground[] = new DrawBlock[TILES_PER_SCREEN];
	private DrawBlock drawBlocksF[] = new DrawBlock[TILES_PER_SCREEN];
	
	private boolean dirtyBlocks[] = new boolean[TILES_PER_SCREEN + TILES_PER_ROW];
	
	private void buildDrawList() {
		debugScreen(currentScreen);
		
		// clear transient background
		for(int i=0; i<drawBlocksExtraBackground.length; i++) {
			drawBlocksExtraBackground[i] = null;
		}
		
		boolean drawDoor = false;
		int doorLeft = 0; // merge with previews as in flag?
		int doorBase = 0;
		
		int neighborBlocksOffset[] = getNeighborBlocksOffset(currentScreen); 
		int screenOffset = currentScreen * TILES_PER_SCREEN;
		int tileIndex = 0;
		int extraIndex = 0;
		for(int row = ROWS-1; row>=0; row--) {
			boolean isLastRow = row == ROWS-1;
			int linearPosBase = row*TILES_PER_ROW;
			int bottom = (row+1) * TILE_HEIGHT + 2;
			
			DrawBlock drawBlock = null;
			
			if (dirtyBlocks[tileIndex]) {
				// draw right side of left screen
				int leftObjOffset = neighborBlocksOffset[TILES_PER_ROW*2 + row];
				int leftObjId = leftObjOffset>=0?type[leftObjOffset] & 0x1F: OBJID_BLOCK;
				int leftDownObjOffset = isLastRow?neighborBlocksOffset[neighborBlocksOffset.length-1]:neighborBlocksOffset[TILES_PER_ROW*2 +row +1]; // top right block at bottom screen or just the object in this screen
				int leftDownObjId = leftDownObjOffset>=0?(type[leftDownObjOffset] & 0x1F):OBJID_BLOCK;
				
				int leftObjC = piecec[leftDownObjId];
				int leftObjB = pieceb[leftObjId];
				int leftObjBMask = maskb[leftObjId];
				int leftObjBy = pieceby[leftObjId];
				
				System.out.println("row " + row + " leftObjB " + leftObjB + " leftObjBMask " + leftObjBMask + " leftDownObjId " + leftDownObjId + " leftObjC " + leftObjC);
	
				drawBlock = new DrawBlock();
				drawBlock.piece = leftObjC;
				drawBlock.bottom = bottom;
				drawBlock.left = 0;
				drawBlocksC[tileIndex] = drawBlock;
				
				drawBlock = new DrawBlock();
				drawBlock.piece = leftObjB;
				drawBlock.mask = leftObjBMask;
				drawBlock.bottom = bottom + leftObjBy;
				drawBlock.left = 0;
				drawBlocksB[tileIndex] = drawBlock;
			}
			
			for(int i=0; i<TILES_PER_ROW; i++) {
				if (!dirtyBlocks[tileIndex]) {
					tileIndex++;
					continue;
				}
				dirtyBlocks[tileIndex] = false;
				
				boolean isLastCol = i == (TILES_PER_ROW-1);
				boolean isFirstCol = i == 0;
				int linearPos = linearPosBase+i;
				int left = i * TILE_WIDTH;
				int objid = type[screenOffset + linearPos] & 0x1F;
				System.out.println(String.format("screenOffset: %d, linearPos:%d, objid:%d", screenOffset, linearPos, objid));
				
				int objidLeftBottomOffset = isLastRow?
						neighborBlocksOffset[TILES_PER_ROW + i]: // bottom screen
						screenOffset + linearPos + TILES_PER_ROW; // next row
				int objidC = (!isLastCol && objidLeftBottomOffset>=0)? type[objidLeftBottomOffset] & 0x1F:0;
						
				int objA = piecea[objid];
				int objB = isLastCol?0:pieceb[objid];
				int objC = piecec[objidC];
				int objD = pieced[objid];
				int objF = fronti[objid];
				int objAmask = maska[objid];
				int objBmask = maskb[objid];
				int objFmask = maskFront[objid];
				int objAy = pieceay[objid];
				int objBy = pieceby[objid];
				int objFy = fronty[objid];
				int objFx = frontx[objid];

				
				if (objid == OBJID_BLOCK) { 
					int objIdRightOffset = isLastCol?neighborBlocksOffset[TILES_PER_ROW*2+ROWS+row]:screenOffset+linearPos+1;
					int objidRight = objIdRightOffset>=0?type[objIdRightOffset] & 0x1F:0;
					if (objidRight!=OBJID_BLOCK) {
						objF = TILE_BLOCK_RIGHT;
						objD = TILE_BLOCK_D_RIGHT;
					}
					int objIdLeftOffset = isFirstCol?neighborBlocksOffset[TILES_PER_ROW*2+row]:screenOffset+linearPos-1;
					int objidLeft = objIdLeftOffset>=0?type[objIdLeftOffset] & 0x1F:0;
					if (objidLeft!=OBJID_BLOCK) {
						objF = TILE_BLOCK_LEFT;
						objD = TILE_BLOCK_D_LEFT;
					}
				} else if (objid == OBJID_LOOSE) {
					int trob = addTROB(currentScreen, linearPos, objid);
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
				if (!isLastCol) {
					drawBlock = new DrawBlock();
					drawBlock.piece = objC;
					drawBlock.bottom = bottom;
					drawBlock.left = nextLeft;
					drawBlocksC[tileIndex+1] = drawBlock;
					
					drawBlock = new DrawBlock();
					drawBlock.piece = objB;
					drawBlock.mask = objBmask;
					drawBlock.bottom = bottom + objBy;
					drawBlock.left = nextLeft;
					drawBlocksB[tileIndex+1] = drawBlock;
				}
				//currentObjId = objidC;
				//if (drawC) drawTileBaseBottom(screenView, bottom, nextLeft, objC);
				currentObjId = objid;
				//if (drawB) drawTileBaseBottom(screenView, bottom + objBy, nextLeft, objB, objBmask, false);
				drawBlock = new DrawBlock();
				drawBlock.piece = objD;
				drawBlock.bottom = bottom;
				drawBlock.left = left;
				drawBlocksD[tileIndex] = drawBlock;
				//if (drawD) drawTileBaseBottom(screenView, bottom, left, objD);
				drawBlock = new DrawBlock();
				drawBlock.piece = objA;
				drawBlock.mask = objAmask;
				drawBlock.bottom = bottom-3 + objAy;
				drawBlock.left = left;
				drawBlocksA[tileIndex] = drawBlock;

				//if (drawA) drawTileBaseBottom(screenView, bottom-3 + objAy, left, objA, objAmask, false);
				if (objid == OBJID_GATE_RIGHT) {
					drawBlock = new DrawBlock();
					drawBlock.piece = 0x6A;
					drawBlock.bottom = bottom-14;
					drawBlock.left = left+4;
					drawBlocksExtraBackground[extraIndex++] = drawBlock;
					//drawTileBaseBottom(screenView, bottom-14, left+4, 0x6A, 0, false); // draw steps
					drawDoor = !doorOpened;
					doorLeft = left+5;
					doorBase = bottom-15;
				}
				
				drawBlock = new DrawBlock();
				drawBlock.piece = objF;
				drawBlock.mask = objFmask;
				drawBlock.bottom = bottom + objFy;
				drawBlock.left = left + objFx;
				drawBlocksF[tileIndex] = drawBlock;
				
				//if (drawF) drawTileBaseBottom(screenView, bottom + objFy, left + objFx, objF, objFmask, objFmask==0);
				
				tileIndex++;
			}
		}
		if (drawDoor) {
			int doorHeight = DOOR_HEIGHT - doorPosition;
			doorBase -= doorPosition;
			while(doorHeight>=DOOR_LINE_HEIGHT) {
				DrawBlock drawBlock = new DrawBlock();
				drawBlock.piece = 0x6C;
				drawBlock.bottom = doorBase;
				drawBlock.left = doorLeft;
				drawBlocksExtraBackground[extraIndex++] = drawBlock;
				
				//drawTileBaseBottom(screenView, doorBase, doorLeft, 0x6C, 0, false);
				doorBase-=DOOR_LINE_HEIGHT;
				doorHeight-=DOOR_LINE_HEIGHT;
			}
			if (doorHeight>0) {
				DrawBlock drawBlock = new DrawBlock();
				drawBlock.piece = 0x6C;
				drawBlock.bottom = doorBase;
				drawBlock.left = doorLeft;
				drawBlock.height = doorHeight;
				drawBlocksExtraBackground[extraIndex++] = drawBlock;
				//drawTileBaseBottom(screenView, doorBase, doorLeft, 0x6C, 0, false, doorHeight);
			}
			drawDoor = false;
		}
		
		// draw all D blocks of screen above
		for(int i=0; i<TILES_PER_ROW; i++) {
			if (!dirtyBlocks[tileIndex+i]) continue;
			dirtyBlocks[tileIndex+i] = false;
			
			int topOffset = neighborBlocksOffset[i];
			int objid = topOffset>=0?type[topOffset] & 0x1F:OBJID_BLOCK;
			int objD = pieced[objid];
			int left = i * TILE_WIDTH;
			DrawBlock drawBlock = new DrawBlock();
			drawBlock.piece = objD;
			drawBlock.bottom = 3;
			drawBlock.left = left;
			drawBlocksD[tileIndex+i] = drawBlock;
			//drawTileBaseBottom(screenView, 3, left, objD);
		}
		
	}

	// get address of neighbor blocks
	private int[] getNeighborBlocksOffset(int screen) {
		// returns neighbor blocks in this order: top, bottom, left, right, bottomLeft
		int result[] = new int[TILES_PER_ROW*2 + ROWS*2 + 1];
		int base = screen * 4;
		int screenTop    = map[base + MAP_TOP] - 1;
		int screenLeft   = map[base + MAP_LEFT] - 1;
		int screenRight  = map[base + MAP_RIGHT] - 1;
		int screenBottom = map[base + MAP_BOTTOM] - 1;
		
		// get screen at bottom left just to get the C part of the top right block
		int screenBottomLeft = screenLeft>=0?map[screenLeft*4 + MAP_BOTTOM]:(screenBottom>=0?map[screenBottom*4 + MAP_LEFT]:-1);
		
		System.out.println("screenBottom " + screenBottom);
		
		int i=0;
		// top screen
		int offset = screenTop * TILES_PER_SCREEN;
		for(int col=0; col<TILES_PER_ROW;col++) { // got bottom line
			result[i++] = screenTop>=0? (offset + (ROWS-1) * TILES_PER_ROW + col) : -1;
		}
		
		// bottom screen
		offset = screenBottom * TILES_PER_SCREEN;
		for(int col=0; col<TILES_PER_ROW;col++) {
			result[i++] = screenBottom>=0? (offset +  col): -1;
		}
		
		// left screen
		offset = screenLeft * TILES_PER_SCREEN;
		for(int row=0; row<ROWS;row++) {
			result[i++] = screenLeft>=0? (offset + ((row+1) * TILES_PER_ROW) - 1) : -1;
		}

		// right screen
		offset = screenRight * TILES_PER_SCREEN;
		for(int row=0; row<ROWS;row++) {
			result[i++] = screenRight>=0? (offset + ((row) * TILES_PER_ROW)) : -1;
		}
		
		// top right block of bottom left screen.  Used to get the C block
		offset = screenBottomLeft * TILES_PER_SCREEN;
		result[i] = screenBottomLeft>=0?(offset + TILES_PER_ROW - 1): -1;
		
		return result;
	}

	private void drawTileBaseBottom(ScreenView screenView, int bottom, int left, int tileId) {
		drawTileBaseBottom(screenView, bottom, left, tileId, 0, false, 0);
	}

	private void drawTileBaseBottom(ScreenView screenView, int bottom, int left, int tileId, int tileMaskId, boolean autoMask) {
		drawTileBaseBottom(screenView, bottom, left, tileId, tileMaskId, autoMask, 0);
	}

	private void drawTileBaseBottom(ScreenView screenView, int bottom, int left, int tileId, int tileMaskId, boolean autoMask, int height) {
		Image image = tiles.get(tileId);
		if (image == null) {
			if (tileId!=0) System.out.println(String.format("Tile %d not found for object id %d", tileId, currentObjId));
			return;
		}
		
		Image mask = tiles.get(tileMaskId);
		if (mask == null) {
			if (tileMaskId!=0 && tileMaskId!=0xff) System.out.println(String.format("Mask %d not found for object id %d", tileMaskId, currentObjId));
		}
		
		image.renderBottom(screenView, bottom, left, mask, autoMask, height);
	}
	
	public void moveLeft() {
		int nextScreen   = map[currentScreen*4 + MAP_LEFT] - 1;
		if (nextScreen>=0) setCurrentScreen(nextScreen);
	}

	public void moveRight() {
		int nextScreen  = map[currentScreen*4 + MAP_RIGHT] - 1;
		if (nextScreen>=0) setCurrentScreen(nextScreen);
	}

	public void moveUp() {
		int nextScreen    = map[currentScreen*4 + MAP_TOP] - 1;
		if (nextScreen>=0) setCurrentScreen(nextScreen);
	}

	public void moveDown() {
		int nextScreen = map[currentScreen*4 + MAP_BOTTOM] - 1;
		if (nextScreen>=0) setCurrentScreen(nextScreen);
	}

	private void setCurrentScreen(int nextScreen) {
		if (nextScreen == currentScreen) return;
		currentScreen = nextScreen;
		for(int i=0; i<dirtyBlocks.length; i++) dirtyBlocks[i] = true;
		buildDrawList();
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
