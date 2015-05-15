


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
		jmp changeScreenEnd
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
		asl
		asl
		tax
		inx					; workaround for ATASM forward bug
		inx
		inx
		jmp getScreen
		
getScreenLeft
		lda levelScreen
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