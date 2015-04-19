.bank

SDLSTL = 560
tileIndex = 210
vramIndex = 212

* = $600

start
		lda #<displayList
		sta SDLSTL
		
		lda #>displayList
		sta SDLSTL+1
		
		lda #<tiles
		sta tileIndex
		lda #>tiles
		sta tileIndex+1

		lda #<vram
		sta vramIndex
		lda #>vram
		sta vramIndex+1
		
		ldy #0
		lda (tileIndex),y
		sta tileWidth
		iny
		lda (tileIndex),y
		tax					; x = height
		
copyTileScan		
		ldy #2
		lda (tileIndex),y
		sta (vramIndex),y
		iny
		lda (tileIndex),y
		sta (vramIndex),y
		
		clc
		lda tileIndex
		adc #2
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
		
endCopy jmp endCopy		

test	
		lda 559
		sta dma
		
		lda #0
		sta 559
		sta 54272
		 
		lda #<displayList
		sta 560
		
		lda #>displayList
		sta 561
		
		lda #<960
		sta 212
		
		lda #>960
		sta 213
		
		lda #<testBuffer
		sta 206
		lda #>testBuffer
		sta 207

		lda #<vram
		sta 210
		lda #>vram
		sta 211
		
		lda 20
		sta frames
		
copyBlock		
		ldy #0
copyStrip		
		lda testBuffer
		sta (210),y
		iny
		lda testBuffer+1
		sta (210),y
		
		iny
		cpy #8
		bne copyStrip
		clc
		lda 210
		adc #8
		sta 210
		lda 211
		adc #0
		sta 211
		
		dec 212
		lda 212
		cmp #255
		bne copyBlock
		dec 213
		lda 213
		cmp #255
		bne copyBlock
		lda 20
		sta frames+1
		lda dma
		sta 559
		sta 54272
		
halt
		jmp halt

dma			.byte 0
frames		.byte 0, 0
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

tiles
		.incbin "images/dungeon/tile_01.pic"

testBuffer
		.byte $AA, $55, $FF, $55, $AA, $00, $FF, $00
vram2	= $b000
vram    = vram2 - (96*40)


.bank
* = $2E0
	.word start
		
	