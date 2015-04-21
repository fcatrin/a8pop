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
tileHeight = 63
tileScanBytes = tileHeight*40

tiles_per_line		= 10
tiles_per_screen	= 3*tiles_per_line

* = $600

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
		
		jsr preRenderMap
		
		ldx #0
		stx renderBlockNumber
				
renderNextBlock		
		txa
		asl
		tay
		sty renderBlockOffset

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
		ldx preRenderBlockDst
		lda piecea,y				; write piecea, pieceb for this block
		sta render_piecea,x
		lda maska,y
		sta render_maska,x
		
		lda preRenderCols
		cmp #1
		beq skipPieceLeft
		
		lda pieceb,y
		sta render_pieceb+1,x
		
		lda preRenderCols
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
		sbc #3*scanbytes
		sta render_piecea_offset,x
		lda vramOffset+1
		sbc #0
		sta render_piecea_offset+1,x
		
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
		pha ; save height for later
		
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
		
		pla
		tax
		
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
		lda tileIndex+1
		adc #0
		sta tileIndex+1
		
		clc
		lda vramIndex
		adc #40
		sta vramIndex
		lda vramIndex+1
		adc #0
		sta vramIndex+1
		dex
		bne copyTileScan
drawTileEnd		
		rts
		
		
; draw tile using mask
; A = tile
; Y = mask		
drawTileMasked
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
		lda maskIndex+1
		adc #0
		sta maskIndex+1
tileMaskShorter		
		clc
		lda tileIndex
		adc #2
		sta tileIndex
		lda tileIndex+1
		adc #0
		sta tileIndex+1
		
		clc
		lda maskIndex
		adc #2
		sta maskIndex
		lda maskIndex+1
		adc #0
		sta maskIndex+1
		
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
		iny
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
		iny
copyNextScanMask
		inc tileMaskDiff
		
		clc
		lda tileIndex
		adc #4
		sta tileIndex
		lda tileIndex+1
		adc #0
		sta tileIndex+1
		
		clc
		lda maskIndex
		adc #4
		sta maskIndex
		lda maskIndex+1
		adc #0
		sta maskIndex+1
		
		clc
		lda vramIndex
		adc #40
		sta vramIndex
		lda vramIndex+1
		adc #0
		sta vramIndex+1
		dex
		bne copyTileMaskedScan
drawTileMaskedEnd		
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

tileWidth 		.byte 0
tileMaskDiff	.byte 0
tileMaskTemp	.byte 0

; DATA

vramOffsets .word tileBaseOffset, tileBaseOffset+tileScanBytes, tileBaseOffset+tileScanBytes*2,tileBaseOffset+tileScanBytes*3
vramOffset	.word 0

blockOffset .word 0, 0, 10, 20

piecea	.byte $00, $01, $00, $07, $00, $00, $00, $00, $00, $00, $00, $00, $00, $00, $00, $00 
		.byte $00, $00, $00, $01, $00, $00, $00, $00, $00, $00, $00, $00, $00, $00, $00, $00
	
pieceb	.byte $00, $02, $00, $08, $00, $00, $00, $00, $00, $00, $00, $00, $00, $00, $00, $00
		.byte $00, $00, $00, $51, $84, $00, $00, $00, $00, $00, $00, $00, $00, $00, $00, $00
		
maska	.byte $00, $03, $00, $A4, $00, $00, $00, $00, $00, $00, $00, $00, $00, $00, $00, $00
		.byte $00, $00, $00, $03, $00, $00, $00, $00, $00, $00, $00, $00, $00, $00, $00, $00
		
maskb	.byte $00, $04, $00, $04, $00, $00, $00, $00, $00, $00, $00, $00, $00, $00, $00, $00
		.byte $00, $00, $00, $04, $00, $00, $00, $00, $00, $00, $00, $00, $00, $00, $00, $00
		
piecec	.byte $00, $00, $00, $09, $0c, $00, $00, $9f, $00, $1d, $00, $00, $9f, $00, $00, $00
		.byte $4f, $50, $00, $00, $85, $00, $00, $93, $94, $00, $00, $00, $00, $00, $00, $00

testmap .byte 0, 0, 0, 1, 1, 1, 1, 1, 20, 20
		.byte 19, 19, 1, 3, 0, 20, 20, 20, 20,20
		.byte 20, 20, 20, 20, 14, 3, 11, 1, 1, 20

xtestmap .byte 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
		.byte 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
		.byte 0,0, 0, 0, 0, 0, 0, 0, 0, 20

render_piecea 
		.rept tiles_per_screen
		.byte 0
		.endr

render_maska 
		.rept tiles_per_screen
		.byte 0
		.endr

render_piecea_offset 
		.rept tiles_per_screen
		.word 0
		.endr

render_pieceb 
		.rept tiles_per_screen
		.byte 0
		.endr
render_maskb 
		.rept tiles_per_screen
		.byte 0
		.endr
render_pieceb_offset 
		.rept tiles_per_screen
		.word 0
		.endr

render_piecec 
		.rept tiles_per_screen
		.byte 0
		.endr
render_piecec_offset 
		.rept tiles_per_screen
		.word 0
		.endr

		
renderBlockNumber	.byte 0
renderBlockOffset	.byte 0
preRenderBlockDst	.byte 0
preRenderBlockSrc	.byte 0
preRenderRow		.byte 0
preRenderCols		.byte 0



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


heightLookup 
		.rept 64
		.word [*-heightLookup]/2*scanbytes
		.endr

tiles   .word      0, tile01, tile02, tile03, tile04, tile05, tile06, tile07
		.word tile08, tile09, tile0a, tile0b, tile0c,      0,      0,      0
		.word      0,      0,      0, tile13,      0, tile15,      0,      0
		.word      0, tile19,      0, tile1b,      0,      0, tile1e, tile1f
		.word tile20, tile21, tile22,      0, tile24,      0, tile26,      0
		.word tile28,      0, tile2a, tile2b, tile2c, tile2d,      0,      0
		.word      0,      0,      0,      0,      0,      0,      0,      0
		.word      0,      0,      0,      0,      0,      0,      0,      0
		.word      0,      0,      0,      0,      0, tile45, tile46,      0
		.word      0,      0,      0, tile4b, tile4c, tile4d, tile4e, tile4f
		.word tile50, tile51,      0,      0,      0,      0,      0,      0
		.word      0,      0,      0,      0,      0,      0,      0,      0
		.word      0,      0,      0,      0,      0,      0,      0,      0
		.word      0,      0, tile6a,      0, tile6c,      0,      0,      0
		.word      0,      0,      0,      0,      0,      0,      0,      0
		.word      0,      0,      0,      0,      0,      0,      0,      0
		.word      0,      0,      0, tile83, tile84, tile85, tile86, tile87
		.word      0,      0,      0,      0,      0,      0,      0,      0
		.word      0,      0,      0,      0,      0,      0, tile97,      0
		.word      0,      0,      0,      0,      0,      0,      0,      0
		.word tilea0, tilea1, tilea2, tilea3, tilea4, tilea5, tilea6, tilea7
		

tile01	.incbin "images/dungeon/tile_01.pic"
tile02	.incbin "images/dungeon/tile_02.pic"
tile03	.incbin "images/dungeon/tile_03.pic"
tile04	.incbin "images/dungeon/tile_04.pic"
tile05	.incbin "images/dungeon/tile_05.pic"
tile06	.incbin "images/dungeon/tile_06.pic"
tile07	.incbin "images/dungeon/tile_07.pic"
tile08	.incbin "images/dungeon/tile_08.pic"
tile09	.incbin "images/dungeon/tile_09.pic"
tile0a	.incbin "images/dungeon/tile_0a.pic"
tile0b	.incbin "images/dungeon/tile_0b.pic"
tile0c	.incbin "images/dungeon/tile_0c.pic"
tile13	.incbin "images/dungeon/tile_13.pic"
tile15	.incbin "images/dungeon/tile_15.pic"
tile19	.incbin "images/dungeon/tile_19.pic"
tile1b	.incbin "images/dungeon/tile_1b.pic"
tile1e	.incbin "images/dungeon/tile_1e.pic"
tile1f	.incbin "images/dungeon/tile_1f.pic"
tile20	.incbin "images/dungeon/tile_20.pic"
tile21	.incbin "images/dungeon/tile_21.pic"
tile22	.incbin "images/dungeon/tile_22.pic"
tile24	.incbin "images/dungeon/tile_24.pic"
tile26	.incbin "images/dungeon/tile_26.pic"
tile28	.incbin "images/dungeon/tile_28.pic"
tile2a	.incbin "images/dungeon/tile_2a.pic"
tile2b	.incbin "images/dungeon/tile_2b.pic"
tile2c	.incbin "images/dungeon/tile_2c.pic"
tile2d	.incbin "images/dungeon/tile_2d.pic"
tile45	.incbin "images/dungeon/tile_45.pic"
tile46	.incbin "images/dungeon/tile_46.pic"
tile4b	.incbin "images/dungeon/tile_4b.pic"
tile4c	.incbin "images/dungeon/tile_4c.pic"
tile4d	.incbin "images/dungeon/tile_4d.pic"
tile4e	.incbin "images/dungeon/tile_4e.pic"
tile4f	.incbin "images/dungeon/tile_4f.pic"
tile50	.incbin "images/dungeon/tile_50.pic"
tile51	.incbin "images/dungeon/tile_51.pic"
tile6a	.incbin "images/dungeon/tile_6a.pic"
tile6c	.incbin "images/dungeon/tile_6c.pic"
tile83	.incbin "images/dungeon/tile_83.pic"
tile84	.incbin "images/dungeon/tile_84.pic"
tile85	.incbin "images/dungeon/tile_85.pic"
tile86	.incbin "images/dungeon/tile_86.pic"
tile87	.incbin "images/dungeon/tile_87.pic"
tile97	.incbin "images/dungeon/tile_97.pic"
tilea0	.incbin "images/dungeon/tile_a0.pic"
tilea1	.incbin "images/dungeon/tile_a1.pic"
tilea2	.incbin "images/dungeon/tile_a2.pic"
tilea3	.incbin "images/dungeon/tile_a3.pic"
tilea4	.incbin "images/dungeon/tile_a4.pic"
tilea5	.incbin "images/dungeon/tile_a5.pic"
tilea6	.incbin "images/dungeon/tile_a6.pic"
tilea7	.incbin "images/dungeon/tile_a7.pic"


vram2	= $b000
vram    = vram2 - (96*40)


.bank
* = $2E0
	.word start
		
	