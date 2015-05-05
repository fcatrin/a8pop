
drawKid
		;.byte 2
		ldx kidFrameIndex
		lda kid_framesL,x
		sta spriteIndex
		lda kid_framesH,x
		sta spriteIndex+1
		
		ldy #0
		lda (spriteIndex),y ; sprite width
		sta spriteWidth
		iny
		lda (spriteIndex),y ; sprite height
		sta spriteHeight
		
		sec
		lda kidY
		sbc spriteHeight
		asl
		tax
		clc
		lda heightLookup,x
		adc #<vram
		adc kidX			; TODO temporaty until we got pixel presicion
		sta vramIndex
		lda heightLookup+1,x
		adc #>vram
		sta vramIndex+1
		
		clc
		lda spriteIndex
		adc #2
		sta spriteIndex
		bcc spriteCopyScan
		inc spriteIndex+1
		
spriteCopyScan		
		ldy spriteWidth
		dey
spriteCopyByte		
		lda (spriteIndex),y
		tax
		lda (vramIndex),y
		and autoMaskTable,x
		ora (spriteIndex),y
		sta (vramIndex),y
		dey
		bpl spriteCopyByte
		
		clc
		lda spriteIndex
		adc spriteWidth
		sta spriteIndex
		bcc noSpriteWidthOverflow
		inc spriteIndex+1
		
noSpriteWidthOverflow		
		clc
		lda vramIndex
		adc #scanBytes
		sta vramIndex
		bcc noSpriteVramOverflow
		inc vramIndex+1
noSpriteVramOverflow
		dec spriteHeight
		bne spriteCopyScan		
		rts

kidX			.byte 11
kidY			.byte 124
kidFrameIndex 	.byte 0
kidTop			.byte 0
spriteWidth		.byte 0
spriteHeight	.byte 0

kid_framesL		.byte <kid_22
kid_framesH		.byte >kid_22

kid_22		.incbin "images/kid/sprite_22.pic"
