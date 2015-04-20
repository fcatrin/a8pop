.bank

SDLSTL = 560
COLOR0 = 708
COLOR1 = 709
COLOR2 = 710

tileIndex = 210
vramIndex = 212

dungeon_color0 = $92
dungeon_color1 = $98
dungeon_color2 = $0D

scanbytes = 40

tileBaseOffset = 3*40
tileHeight = 63
tileScans = tileHeight*40

* = $600

vramOffsets .word tileBaseOffset, tileBaseOffset+tileScans, tileBaseOffset+tileScans*2,tileBaseOffset+tileScans*3
vramOffset	.word 0

piecea .byte 0, 1
pieceb .byte 0, 2



start
		lda #dungeon_color0
		sta COLOR0
		lda #dungeon_color1
		sta COLOR1
		lda #dungeon_color2
		sta COLOR2


		lda #<displayList
		sta SDLSTL
		
		lda #>displayList
		sta SDLSTL+1
		
		ldx #6				; row
		
		clc
		lda vramOffsets,x
		adc #<vram
		sta vramOffset
		lda vramOffsets+1,x
		adc #>vram
		sta vramOffset+1
		
		
		lda #1
		jsr drawTile
		
		clc
		lda vramOffset
		adc #4
		sta vramOffset
		lda vramOffset+1
		adc #0
		sta vramIndex+1
		
		lda #2
		jsr drawTile
		
halt	jmp halt		

; draw a tile on screen (STA method)
; A = tile number
; vramIndex = vram position of first scan

drawTile
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
		clc
		lda vramOffset
		sbc heightLookup,x
		sta vramIndex
		lda vramOffset+1
		sbc heightLookup+1,x
		sta vramIndex+1
		
		pla
		tax
		
copyTileScan		
		ldy #2
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
		
		rts		

tileWidth 	.byte 0


.bank
*		= $4000
displayList
		.byte 112, 112, 112, 
		.byte $4E
		.word vram
		.byte $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E 
		.byte $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E 
		.byte $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E 
		.byte $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E 
		.byte $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E 
		.byte $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E
		.byte $4E
		.word vram2
		.byte $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E 
		.byte $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E 
		.byte $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E 
		.byte $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E 
		.byte $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E 
		.byte $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E, $0E 
		.byte 65
		.word displayList


heightLookup 
		.word scanbytes*0, scanbytes*1, scanbytes*2, scanbytes*3, scanbytes*4, scanbytes*5, scanbytes*6, scanbytes*7
		.word scanbytes*8, scanbytes*9, scanbytes*10, scanbytes*11, scanbytes*12, scanbytes*13, scanbytes*14, scanbytes*15
		.word scanbytes*16, scanbytes*17, scanbytes*18, scanbytes*19, scanbytes*20, scanbytes*21, scanbytes*22, scanbytes*23
		.word scanbytes*24, scanbytes*25, scanbytes*26, scanbytes*27, scanbytes*28, scanbytes*29, scanbytes*30, scanbytes*31
		.word scanbytes*32, scanbytes*33, scanbytes*34, scanbytes*35, scanbytes*36, scanbytes*37, scanbytes*38, scanbytes*39
		.word scanbytes*40, scanbytes*41, scanbytes*42, scanbytes*43, scanbytes*44, scanbytes*45, scanbytes*46, scanbytes*47
		.word scanbytes*48, scanbytes*49, scanbytes*50, scanbytes*51, scanbytes*52, scanbytes*53, scanbytes*54, scanbytes*55
		.word scanbytes*56, scanbytes*57, scanbytes*58, scanbytes*59, scanbytes*60, scanbytes*61, scanbytes*62, scanbytes*63

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
		
	