.bank

SDLSTL = 560
COLOR0 = 708
COLOR1 = 709
COLOR2 = 710

tileIndex = 210
vramIndex = 212

vramSize = 40*192

dungeon_color0 = $94
dungeon_color1 = $8A
dungeon_color2 = $0C

scanbytes = 40

tileBaseOffset = 3*40
tileHeight = 63
tileScans = tileHeight*40

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
		
		lda render_pieceb_offset,y
		sta vramOffset
		lda render_pieceb_offset+1,y
		sta vramOffset+1
		lda render_pieceb,x
		jsr drawTile
		
		ldx renderBlockNumber
		ldy renderBlockOffset
		lda render_piecea_offset,y
		sta vramOffset
		lda render_piecea_offset+1,y
		sta vramOffset+1
		lda render_piecea,x
		jsr drawTile
		
		inc renderBlockNumber
		ldx renderBlockNumber
		cpx #tiles_per_screen
		bne renderNextBlock
		
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
		lda pieceb,y
		sta render_pieceb,x
		
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
		
		clc								; offsetB = offset + 4 bytes (next tile col)
		lda vramOffset
		adc #4
		sta vramOffset
		sta render_pieceb_offset,x
		lda vramOffset+1
		adc #0
		sta vramOffset+1
		sta render_pieceb_offset+1,x
		
		inc preRenderBlockSrc
		inc preRenderBlockDst
		dec preRenderCols
		bne preRenderNextBlock
		
		ldx preRenderRow
		dex
		dex
		stx preRenderRow
		bne preRenderNextRow
		rts


; draw a tile on screen (STA method)
; A = tile number
; vramIndex = vram position of first scan

drawTile
		cmp #0
		beq drawTileEnd
		
		asl
		tax
		
		lda tiles,x
		sta tileIndex
		lda tiles+1,x
		sta tileIndex+1
		
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

tileWidth 	.byte 0

; DATA

vramOffsets .word tileBaseOffset, tileBaseOffset+tileScans, tileBaseOffset+tileScans*2,tileBaseOffset+tileScans*3
vramOffset	.word 0

blockOffset .word 0, 0, 10, 20

piecea .byte 0, 1
pieceb .byte 0, 2

testmap .byte 0, 0, 0, 0, 0, 0, 0, 1, 1, 1
		.byte 0, 0, 0, 0, 1, 1, 1, 0, 0, 0
		.byte 1, 1, 1, 1, 0, 0, 0, 0, 0, 0

render_piecea 
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
render_pieceb_offset 
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

tiles   .word 0, tile01, tile02, tile03, tile04

tile01	.incbin "images/dungeon/tile_01.pic"
tile02	.incbin "images/dungeon/tile_02.pic"
tile03	.incbin "images/dungeon/tile_03.pic"
tile04	.incbin "images/dungeon/tile_04.pic"

vram2	= $b000
vram    = vram2 - (96*40)


.bank
* = $2E0
	.word start
		
	