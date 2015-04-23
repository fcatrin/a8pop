.bank

SDLSTL = 560
COLOR0 = 708
COLOR1 = 709
COLOR2 = 710

maskIndex = 208
tileIndex = 210
vramIndex = 212

vramSize = 40*192

dungeon_color0 = $94
dungeon_color1 = $8A
dungeon_color2 = $0C

scanbytes = 40

tileBaseOffset = 3*40
stdTileHeight = 63
tileScanBytes = stdTileHeight*40

tiles_per_line		= 10
tiles_per_screen	= 3*tiles_per_line

* = $600

frame0 .byte 0
frame1 .byte 0
frame2 .byte 0

start

		lda #dungeon_color0		; setup colors
		sta COLOR0
		lda #dungeon_color1
		sta COLOR1
		lda #dungeon_color2
		sta COLOR2


		lda #<displayList		; setup display list
		sta SDLSTL
		
		lda #>displayList
		sta SDLSTL+1
		
		jsr clearScreen
		jsr genAutoMaskTable
		
		lda 20
		sta frame0
		jsr preRenderMap
		lda 20
		sta frame1
		
		ldx #0
		ldy #0
drawNextC		
		stx renderBlockNumber
		sty renderBlockOffset
		lda render_piecec_offset,y
		sta vramOffset
		lda render_piecec_offset+1,y
		sta vramOffset+1
		ldy #0
		lda render_piecec,x
		jsr drawTile
		ldy renderBlockOffset
		iny
		iny
		ldx renderBlockNumber
		inx
		cpx #tiles_per_screen
		bne drawNextC

		ldx #0
		ldy #0		
drawNextB		
		stx renderBlockNumber
		sty renderBlockOffset
		lda render_pieceb_offset,y
		sta vramOffset
		lda render_pieceb_offset+1,y
		sta vramOffset+1
		ldy render_maskb,x
		lda render_pieceb,x
		jsr drawTile
		ldy renderBlockOffset
		iny
		iny
		ldx renderBlockNumber
		inx
		cpx #tiles_per_screen
		bne drawNextB
		
		ldx #0
		ldy #0		
drawNextA		
		stx renderBlockNumber
		sty renderBlockOffset
		lda render_piecea_offset,y
		sta vramOffset
		lda render_piecea_offset+1,y
		sta vramOffset+1
		ldy render_maska,x
		lda render_piecea,x
		jsr drawTile
		
		ldy renderBlockOffset
		iny
		iny
		ldx renderBlockNumber
		inx
		cpx #tiles_per_screen
		bne drawNextA
		
		
		ldx #0
		ldy #0		
drawNextD		
		stx renderBlockNumber
		sty renderBlockOffset
		lda render_pieced_offset,y
		sta vramOffset
		lda render_pieced_offset+1,y
		sta vramOffset+1
		ldy #0
		lda render_pieced,x
		jsr drawTile
		
		ldy renderBlockOffset
		iny
		iny
		ldx renderBlockNumber
		inx
		cpx #tiles_per_screen
		bne drawNextD	
		
		ldx #0
		ldy #0		
drawNextF		
		stx renderBlockNumber
		sty renderBlockOffset
		lda render_piecef_offset,y
		sta vramOffset
		lda render_piecef_offset+1,y
		sta vramOffset+1
		ldy render_maskf,x
		lda render_piecef,x
		jsr drawTile
		
		ldy renderBlockOffset
		iny
		iny
		ldx renderBlockNumber
		inx
		cpx #tiles_per_screen
		bne drawNextF		
			
		lda 20
		sta frame2
		
halt	jmp halt		

; Pre Render Map
; Create lookup tables for each tile pieces with screen offset

preRenderMap
		lda #0
		sta preRenderBlockDst

		ldx #6					; start in third row
		stx preRenderRow

preRenderNextRow		
		lda blockOffset,x
		sta preRenderBlockSrc
		
		lda #10
		sta preRenderCols
		
		clc						; set vramOffset for row on x
		lda vramOffsets,x
		adc #<vram
		sta vramOffset
		lda vramOffsets+1,x
		adc #>vram
		sta vramOffset+1

preRenderNextBlock
		ldx preRenderBlockSrc		; load block number from map
		lda testmap,x
		tay
		ldx preRenderBlockDst		; y = block id, x = tile position
		lda piecea,y				; write piecea, maska for this block
		sta render_piecea,x
		lda maska,y
		sta render_maska,x
		lda pieced,y
		sta render_pieced,x
		lda piecef,y
		sta render_piecef,x
		lda maskf,y
		sta render_maskf,x
		
		lda preRenderCols			; last column dont need pieceb and piecec (all pieces to the left)
		cmp #1
		beq skipPieceLeft
		
		lda pieceb,y				; write pieceb, maskb for this block
		sta render_pieceb+1,x
		lda maskb,y
		sta render_maskb+1,x
		
		lda preRenderCols			; write piecec for this block only in rows 2 & 3
		cmp #20
		bpl skipPieceLeft
		lda piecec,y
		sta render_piecec+10,x
		
skipPieceLeft		
		txa								; write offsets. X = offset index
		asl
		tax
		
		sec								; offsetA = offset - 3 scanlines
		lda vramOffset
		sta render_pieced_offset,x
		sbc #3*scanbytes
		sta render_piecea_offset,x
		sta render_piecef_offset,x
		lda vramOffset+1
		sta render_pieced_offset+1,x
		sbc #0
		sta render_piecea_offset+1,x
		sta render_piecef_offset+1,x
		
		lda preRenderCols
		cmp #20
		bpl preRenderNextLine
				
		clc								; offsetB = offset + 4 bytes (next tile col)
		lda vramOffset					; offsetC = offsetB (for now)
		adc #4
		sta vramOffset
		sta render_pieceb_offset+2,x
		sta render_piecec_offset,x
		lda vramOffset+1
		adc #0
		sta vramOffset+1
		sta render_pieceb_offset+1,x
		sta render_piecec_offset+1,x
		
		inc preRenderBlockSrc
		inc preRenderBlockDst
		dec preRenderCols
		beq preRenderNextLine
		jmp preRenderNextBlock
preRenderNextLine		
		ldx preRenderRow
		dex
		dex
		stx preRenderRow
		beq preRenderEnd
		jmp preRenderNextRow
preRenderEnd		
		rts


; draw a tile on screen (STA method)
; A = tile number
; vramIndex = vram position of first scan

drawTile
		cmp #0
		bne validTile
		rts
validTile
		
		cpy #0
		bne drawTileMasked		; use slower version with masking

		jsr getTileAddress
		sta tileIndex
		sty tileIndex+1
		
		ldy #0
		lda (tileIndex),y
		sta tileWidth
		iny
		lda (tileIndex),y
		sta tileHeight
		
		asl
		tax
		sec
		lda vramOffset
		sbc heightLookup,x
		sta vramIndex
		lda vramOffset+1
		sbc heightLookup+1,x
		sta vramIndex+1
		
		clc
		lda tileIndex
		adc #2
		sta tileIndex
		lda tileIndex+1
		adc #0
		sta tileIndex+1
		
		ldx tileHeight
		
copyTileScan		
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
		bcc incVRAMIndex
		inc tileIndex+1
		clc
incVRAMIndex		
		lda vramIndex
		adc #40
		sta vramIndex
		bcc evalNextScan
		inc vramIndex+1
evalNextScan		
		dex
		bne copyTileScan
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
		sty tileMaskTemp
		
		jsr getTileAddress
		sta tileIndex
		sty tileIndex+1
		
		lda tileMaskTemp
		jsr getTileAddress
		sta maskIndex
		sty maskIndex+1
		
		ldy #0
		lda (tileIndex),y
		sta tileWidth
		iny
		lda (tileIndex),y
		sta tileHeight
		
		asl
		tax
		sec
		lda vramOffset
		sbc heightLookup,x
		sta vramIndex
		lda vramOffset+1
		sbc heightLookup+1,x
		sta vramIndex+1
		
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
		cmp #0
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
		jsr getTileAddress
		sta tileIndex
		sty tileIndex+1
		
		ldy #0
		lda (tileIndex),y
		sta tileWidth
		iny
		lda (tileIndex),y
		sta tileHeight
		
		asl
		tax
		sec
		lda vramOffset
		sbc heightLookup,x
		sta vramIndex
		lda vramOffset+1
		sbc heightLookup+1,x
		sta vramIndex+1
		
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



; Return tile address in a,y
getTileAddress
		asl
		bcc getTileAddressNear
		tax
		lda tiles+256,x
		ldy tiles+257,x
		rts
getTileAddressNear
		tax
		lda tiles,x
		ldy tiles+1,x
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
		
		.include "mask.asm"
		.include "bgdata.asm"

.bank
*		= $4000
displayList
		.byte 112, 112, 112, 
		.byte $4E
		.word vram
		.rept 95
		.byte $0E
		.endr 
		.byte $4E
		.word vram2
		.rept 95
		.byte $0E
		.endr 
		.byte 65
		.word displayList

vram2	= $b000
vram    = vram2 - (96*40)


.bank
* = $2E0
	.word start
		
	