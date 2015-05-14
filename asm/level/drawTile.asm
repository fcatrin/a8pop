; draw a tile on screen (STA method)
; A = tile number
; vramIndex = vram position of first scan

; Time Zero  08 1A
; Time Dirty 02 12

; Time without drawTile
; Time Zero  02 29  (4E times)
; Time Dirty 00 5B  (18 times)

drawTile
		cmp #0
		bne validTile
		rts
validTile
		cpy #0
		bne drawTileMasked		; use slower version with masking

		ldx timesTrackIndex
		sta timesTrackBase,x
		inc timesTrackIndex
		
		tax
		lda tilesL,x
		sta tileIndex
		clc
		adc #2
		sta tileSrc0+1
		sta tileSrc1+1
		sta tileSrc2+1
		sta tileSrc3+1
		
		lda tilesH,x
		sta tileIndex+1
		adc #0
		sta tileSrc0+2
		sta tileSrc1+2
		sta tileSrc2+2
		sta tileSrc3+2
		
		ldy #1
		lda (tileIndex),y
		sta tileHeight
		
		ldx #0		
		ldy #0
copyTileScan		
tileSrc0		
		lda $FFFF,x
		sta (vramIndex),y
		iny
		inx
tileSrc1
		lda $FFFF,x
		sta (vramIndex),y
		iny
		inx
tileSrc2		
		lda $FFFF,x
		sta (vramIndex),y
		iny
		inx
tileSrc3		
		lda $FFFF,x
		sta (vramIndex),y
		inx

		dec tileHeight
		beq drawTileEnd
		
		tya
		clc
		adc #37
		tay
		bcc copyTileScan
		inc vramIndex+1
		jmp copyTileScan

drawTileEnd
		rts


; draw tile using mask
; A = tile
; Y = mask		
drawTileMasked
		cpy #255
		bne drawTileRegularMask
		jmp drawTileAutoMasked
drawTileRegularMask		

		ldx timesTrackIndex
		sta timesTrackBase,x
		inc timesTrackIndex
	
		tax
		lda tilesL,x
		sta tileIndex
		lda tilesH,x
		sta tileIndex+1
		
		lda tilesL,y
		sta maskIndex
		lda tilesH,y
		sta maskIndex+1
		
		ldy #1
		lda (tileIndex),y
		sta tileHeight
		
		ldy #1
		lda (maskIndex), y
		sec
		sbc tileHeight
		sta tileMaskDiff		; tileMaskDiff = tileHeight - maskHeight
		bmi tileMaskShorter
		asl						; maskIndex += tileMaskDiff*4
		asl
		clc
		adc maskIndex
		sta maskIndex
		bcc tileMaskShorter
		inc maskIndex+1
tileMaskShorter		
		clc
		lda tileIndex
		adc #2
		sta tileIndex
		bcc incMaskIndex
		inc tileIndex+1
		clc
incMaskIndex		
		lda maskIndex
		adc #2
		sta maskIndex
		bcc beginCopy
		inc maskIndex+1

beginCopy		
		ldx tileHeight
		
copyTileMaskedScan		
		ldy #0
		lda tileMaskDiff
		bpl copyWithMask
		lda (tileIndex),y
		sta (vramIndex),y
		iny
		lda (tileIndex),y
		sta (vramIndex),y
		iny
		lda (tileIndex),y
		sta (vramIndex),y
		iny
		lda (tileIndex),y
		sta (vramIndex),y
		jmp copyNextScanMask
copyWithMask
		lda (vramIndex),y
		and (maskIndex),y
		ora (tileIndex),y
		sta (vramIndex),y
		iny
		lda (vramIndex),y
		and (maskIndex),y
		ora (tileIndex),y
		sta (vramIndex),y
		iny
		lda (vramIndex),y
		and (maskIndex),y
		ora (tileIndex),y
		sta (vramIndex),y
		iny
		lda (vramIndex),y
		and (maskIndex),y
		ora (tileIndex),y
		sta (vramIndex),y
copyNextScanMask
		clc
		lda tileIndex
		adc #4
		sta tileIndex
		bcc checkTileMaskDiff
		inc tileIndex+1
		
checkTileMaskDiff		
		lda tileMaskDiff
		cmp #0
		bmi skipMaskScanline
		
		clc
		lda maskIndex
		adc #4
		sta maskIndex
		bcc skipMaskScanLine
		inc maskIndex+1

skipMaskScanline
		inc tileMaskDiff
		
		clc
		lda vramIndex
		adc #40
		sta vramIndex
		bcc prepareNextMaskedScan
		inc vramIndex+1
prepareNextMaskedScan		
		dex
		bne copyTileMaskedScan
drawTileMaskedEnd		
		rts

; draw tile in A using automasking
drawTileAutoMasked

		ldx timesTrackIndex
		sta timesTrackBase,x
		inc timesTrackIndex

		tax
		lda tilesL,x
		sta tileIndex
		lda tilesH,x
		sta tileIndex+1
		
		ldy #1
		lda (tileIndex),y
		sta tileHeight
		
		clc
		lda tileIndex
		adc #2
		sta tileIndex
		bcc copyTileAutoMaskedScan
		inc tileIndex+1
	
copyTileAutoMaskedScan		
		ldy #0
		lda (tileIndex),y
		tax
		lda (vramIndex),y
		and autoMaskTable,x
		ora (tileIndex),y
		sta (vramIndex),y
		iny

		lda (tileIndex),y
		tax
		lda (vramIndex),y
		and autoMaskTable,x
		ora (tileIndex),y
		sta (vramIndex),y
		iny

		lda (tileIndex),y
		tax
		lda (vramIndex),y
		and autoMaskTable,x
		ora (tileIndex),y
		sta (vramIndex),y
		iny

		lda (tileIndex),y
		tax
		lda (vramIndex),y
		and autoMaskTable,x
		ora (tileIndex),y
		sta (vramIndex),y

		clc
		lda tileIndex
		adc #4
		sta tileIndex
		bcc nextScanAutoMask
		inc tileIndex+1
		
		clc
nextScanAutoMask		
		lda vramIndex
		adc #40
		sta vramIndex
		bcc prepareNextAutoMaskedScan
		inc vramIndex+1
prepareNextAutoMaskedScan		
		dec tileHeight
		bne copyTileAutoMaskedScan
		rts


clearScreen
		lda #<vram
		sta vramIndex
		lda #>vram
		sta vramIndex+1
		
		ldx #>vramSize
		ldy #0
		lda #0
clearBlock		
		sta (vramIndex),y
		iny
		bne clearBlock
		inc vramIndex+1
		dex
		bne clearBlock
		
		ldy #<vramSize
clearLastBlock		
		sta (vramIndex),y
		dey
		bne clearLastBlock
		rts
		
; special drawing for C tile + clearing

drawTileC
		cmp #0
		bne clearAndDrawC

		ldx #stdTileHeight
		ldy #0
clearOnlyNextScanC		
		lda #0
		sta (vramIndex),y
		iny
		sta (vramIndex),y
		iny
		sta (vramIndex),y
		iny
		sta (vramIndex),y
		dex
		beq clearOnlyEnd
		tya
		sec
		sbc #scanBytes+3
		tay
		bcs clearOnlyNextScanC
		dec vramIndex+1
		jmp clearOnlyNextScanC
clearOnlyEnd		
		rts
		
clearAndDrawC
		ldx timesTrackIndex
		sta timesTrackBase,x
		inc timesTrackIndex
	
		tax
		lda tilesL,x
		sta tileIndex
		lda tilesH,x
		sta tileIndex+1
		
		ldy #1
		sec
		lda #stdTileHeight
		sbc (tileIndex),y
		beq tileCIsFullSize

		sta tileHeight
		lda vramIndex
		pha
		lda vramIndex+1
		pha
clearNextScanC		
		sec
		lda vramIndex
		sbc #scanBytes
		sta vramIndex
		lda vramIndex+1
		sbc #0
		sta vramIndex+1
		
		lda #0
		ldy #0
		sta (vramIndex),y
		iny
		sta (vramIndex),y
		iny
		sta (vramIndex),y
		iny
		sta (vramIndex),y
		dec tileHeight
		bne clearNextScanC
		pla
		sta vramIndex+1
		pla
		sta vramIndex
		ldy #1
		
tileCIsFullSize		
		lda (tileIndex),y
		tax
		clc
		lda tileIndex
		adc #2
		sta tileIndex
		bcc copyTileCScan
		inc tileIndex+1
		
copyTileCScan
		ldy #0
		lda (tileIndex),y
		sta (vramIndex),y
		iny
		lda (tileIndex),y
		sta (vramIndex),y
		iny
		lda (tileIndex),y
		sta (vramIndex),y
		iny
		lda (tileIndex),y
		sta (vramIndex),y
		iny
		
		clc
		lda tileIndex
		adc #4
		sta tileIndex
		bcc incVRAMIndexC
		inc tileIndex+1
		clc
incVRAMIndexC		
		lda vramIndex
		adc #40
		sta vramIndex
		bcc evalNextScanC
		inc vramIndex+1
evalNextScanC
		dex
		bne copyTileCScan
drawTileCEnd	
		rts