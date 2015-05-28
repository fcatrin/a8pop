


changeLevel
		lda #<levelData
		sta memcpyDst
		lda #>levelData
		sta memcpyDst+1
		
		lda levelNumber
		asl
		tax
		lda levelDataOffsets,x
		sta memcpySrc
		lda levelDataOffsets+1,x
		sta memcpySrc+1
		
		lda #<levelSize
		sta memcpyLen
		lda #>levelSize
		sta memcpyLen+1
		
		lda #0
		sta levelScreen
		
		jmp memcpy
		
changeScreen
		cmp #255
		bne changeValidScreen
		rts
changeValidScreen
		sta levelScreen
		asl
		tax
		lda levelScreenLookup,x
		sta levelScreenOffset+1
		lda levelScreenLookup+1,x
		sta levelScreenOffset+2
		
		ldy #0
levelScreenOffset
		lda $FFFF,y
		sta screenData,y
		iny
		cpy #levelTilesPerScreen
		bne levelScreenOffset
		
		jsr getScreenUp
		cmp #255
		bne hasScreenTopRow
		lda #TILE_BLOCK
		ldx #levelTilesPerRow
setScreenTopBlock		
		sta screenDataTop,x
		dex
		bpl setScreenTopBlock
		jmp setScreenLeftB
hasScreenTopRow
		asl
		tax		
		clc
		lda levelScreenLookup,x
		adc #2*levelTilesPerRow		; last row
		sta memcpySrc
		lda levelScreenLookup+1,x
		adc #0
		sta memcpySrc+1
		lda #<screenDataTop
		sta memcpyDst
		lda #>screenDataTop
		sta memcpyDst+1
		lda #levelTilesPerRow
		sta memcpyLen
		lda #0
		sta memcpyLen+1
		jsr memcpy
		
setScreenLeftB
		lda #TILE_BLOCK				; default values if there is no screen at left or bottom/left (for C+2)
		sta screenDataLeftB+0
		sta screenDataLeftB+1
		sta screenDataLeftB+2
		sta screenDataLeftC+0
		sta screenDataLeftC+1

		jsr getScreenLeft
		cmp #255
		beq getScreenBottom
		asl
		tax
		lda levelScreenLookup,x
		sta memcpySrc
		lda levelScreenLookup+1,x
		sta memcpySrc+1
		ldy #levelTilesPerRow-1      ; last column from first row
		lda (memcpySrc),y
		sta screenDataLeftB+0
		ldy #(levelTilesPerRow*2)-1  ; last column from second row
		lda (memcpySrc),y
		sta screenDataLeftB+1
		sta screenDataLeftC+0
		ldy #(levelTilesPerRow*3)-1  ; last column from third row
		lda (memcpySrc),y
		sta screenDataLeftB+2
		sta screenDataLeftC+1

		jsr getScreenLeft			 ; look for screen at left -> down
		jsr getScreenDownFromA
		cmp #255
		bne setScreenBottomC
		
getScreenBottom		
		jsr getScreenDown			 ; look for screen at down -> left
		jsr getScreenLeftFromA
		cmp #255
		beq copyScreenBottomTopLine
		
setScreenBottomC		
		asl
		tax
		lda levelScreenLookup,x
		sta memcpySrc
		lda levelScreenLookup+1,x
		sta memcpySrc+1
		ldy #levelTilesPerRow-1      ; last column from first row
		lda (memcpySrc),y
		sta screenDataBottomC
		
copyScreenBottomTopLine		
		jsr getScreenDown
		cmp #255
		bne hasScreenBottomTopLine
		ldy #levelTilesPerRow-2
		lda #TILE_BLOCK
setTileBlockBottom		
		sta screenDataBottomC,y
		dey
		bpl setTileBlockBottom
		jmp changeScreenEnd
		
hasScreenBottomTopLine		
		asl
		tax
		lda levelScreenLookup,x
		sta memcpySrc
		lda levelScreenLookup+1,x
		sta memcpySrc+1
		ldy #levelTilesPerRow-2
copyScreenBottomC		
		lda (memcpySrc),y
		sta screenDataBottomC+1,y
		dey
		bpl copyScreenBottomC
		
changeScreenEnd	

		lda #1
		sta levelScreenChanged	
		jsr dirtySetAll
		jsr preRenderMap
		jsr preRenderOffsets
		rts
		
		
dirtyClearAll
		ldx #0
		lda #0
		sta levelScreenDirty
		beq dirtyLoop
		
dirtySetAll
		ldx #0
		lda #255
				
dirtyLoop		
		sta render_dirty_blocks,x
		inx
		cpx #levelTilesPerScreen+levelTilesPerRow
		bne dirtyLoop
		rts

getScreenUp
		lda levelScreen
		asl
		asl
		tax
		inx					; workaround for ATASM forward bug
		inx
		jmp getScreen
		
getScreenDown
		lda levelScreen
getScreenDownFromA
		asl
		asl
		tax
		inx					; workaround for ATASM forward bug
		inx
		inx
		jmp getScreen
		
getScreenLeft
		lda levelScreen
getScreenLeftFromA		
		asl
		asl
		tax
		jmp getScreen
		
getScreenRight
		lda levelScreen
		asl
		asl
		tax
		inx					; workaround for ATASM forward bug
		
getScreen		
		lda levelMap,x
		tax
		dex
		txa
		rts
		
		


levelDataOffsets .word levelData0, levelData1
		
levelSize = 2304		
levelData
		.rept levelSize
		.byte 0
		.endr
		
levelData0 ins "../levels/LEVEL0"
levelData1 ins "../levels/LEVEL1"

levelScreenLookup
		.rept levelScreens
		.word levelData+([*-levelScreenLookup]/2*levelTilesPerScreen)
		.endr

levelScreen	.byte 0
levelNumber	.byte 0

levelScreenChanged	.byte 0  ; redraw whole screen
levelScreenDirty	.byte 0  ; redraw dirty blocks

; origin struct
;	byte type[] = new byte[DESCRIPTORS];
;	byte spec[] = new byte[DESCRIPTORS];
;	byte linkMap[] = new byte[MAX_LINK];
;	byte linkLoc[] = new byte[MAX_LINK];
;	byte map[] = new byte[SCREENS*4];
;	byte info[] = new byte[256];		