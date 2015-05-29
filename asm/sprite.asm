
drawKid
		lda kidX
		and #$FC
		lsr
		lsr 
		sta kidXOffset
		
		sec
		lda kidY
		sbc spriteHeight
		tax
		clc
		lda heightLookupL,x
		adc vramBuffer
		sta vramIndex
		lda heightLookupH,x
		adc vramBuffer+1
		sta vramIndex+1
		
		clc
		lda vramIndex
		adc kidXOffset
		sta vramIndex
		bcc spriteCopyScanP1
		inc vramIndex+1

; Original copy with pad 0
		
spriteCopyScanP1
		ldy #0
		sty rotationBuffer
spriteCopyByteP1		
		lda (spriteIndex),y
		tax
		lda rot1TableRight,x
		sta rotationBufferNew
		lda rotationBufferOld
		ora rot1TableLeft,x
		beq spriteNoCopyRot1
		sta rotationBuffer
		tax
		
		lda (vramIndex),y
		and autoMaskTable,x
		ora rotationBuffer
		sta (vramIndex),y
		
spriteNoCopyRot1		
		lda rotationBufferNew
		sta rotationBufferOld
		
		iny
		cpy spriteWidth
		bne spriteCopyByteP1
		; copy remainder byte
		lda rotationBufferOld
		tax
		
		lda (vramIndex),y
		and autoMaskTable,x
		ora rotationBufferOld
		sta (vramIndex),y		
		
	
		clc
		lda spriteIndex
		adc spriteWidth
		sta spriteIndex
		bcc noSpriteWidthOverflowP1
		inc spriteIndex+1
		
noSpriteWidthOverflowP1		
		clc
		lda vramIndex
		adc #scanBytes
		sta vramIndex
		bcc noSpriteVramOverflowP1
		inc vramIndex+1
noSpriteVramOverflowP1
		dec spriteHeight
		bne spriteCopyScanP1	
		rts


; Original copy with pad 0

/*		
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
*/

kidPreRender
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
		
		clc
		lda spriteIndex
		adc #2
		sta spriteIndex
		bcc kidSaveLastBounds
		inc spriteIndex+1


kidSaveLastBounds
		lda boundsKidCurrent
		sta boundsKidPrev
		lda boundsKidCurrent+1
		sta boundsKidPrev+1
		lda boundsKidCurrent+2
		sta boundsKidPrev+2
		lda boundsKidCurrent+3
		sta boundsKidPrev+3

; Save current kid bounds		
		lda kidX
		sta boundsKidCurrent
		lda spriteWidth
		asl
		asl
		clc
		adc kidX
		sta boundsKidCurrent+1
		
		lda kidY
		sta boundsKidCurrent+3
		sec
		sbc spriteHeight
		sta boundsKidCurrent+2
		lda spriteHeight

; Step 1 calc max bounds between prev and current bounds
; Step 2 transform bounds to block positions
; Step 3 mark dirty blocks
; Step 4 notify changes

kidUpdateDirtyBlocks
		lda boundsKidPrev		; x1 = min(prevX1, currentX1)
		cmp boundsKidCurrent
		bcs kidUseX1			
		tax
		bcc kidUsePrevX1
kidUseX1		
		ldx boundsKidCurrent
kidUsePrevX1		
		stx boundsKid
		
		lda boundsKidPrev+1		; x2 = max(prevX2, currentX2)
		cmp boundsKidCurrent+1
		bcc kidUseX2
		tax
		bcs kidUsePrevX2
kidUseX2
		ldx boundsKidCurrent+1
kidUsePrevX2
		stx boundsKid+1

		lda boundsKidPrev+2		; y1 = min(prevY1, currentY1)
		cmp boundsKidCurrent+2
		bcs kidUseY1
		tax
		bcc kidUsePrevY1
kidUseY1		
		ldx boundsKidCurrent+2
kidUsePrevY1		
		stx boundsKid+2
		
		lda boundsKidPrev+3		; y2 = max(prevY2, currentY2)
		cmp boundsKidCurrent+3
		bcc kidUseY2
		tax
		bcs kidUsePrevY2
kidUseY2
		ldx boundsKidCurrent+3
kidUsePrevY2
		stx boundsKid+3
		
		; block x1 = x1 / 16
		lda boundsKid
		and #$F0
		lsr 
		lsr 
		lsr 
		lsr 
		sta dirtyBlockX1
		
		; block x2 = (x2-1) / 16  (width=1 means x1==x2)
		lda boundsKid+1
		sec
		sbc #1
		and #$F0
		lsr 
		lsr 
		lsr 
		lsr 
		adc #1
		sta dirtyBlockX2
		
		; block y1 = (y1 - 3 - 1) / 63  (height=1 means y1==y2)

		lda boundsKid+2
		ldx #0
		sec
		sbc #2		; TODO handle top row
		sbc #63
		bmi boundY1Found
		inx
		sbc #63
		bmi boundY1Found
		inx
boundY1Found
		stx dirtyBlockY1
		
		lda boundsKid+3
		ldx #0
		sec
		sbc #3
		sbc #63
		bmi boundY2Found
		inx
		sbc #63
		bmi boundY2Found
		inx
boundY2Found
		inx
		stx dirtyBlockY2
		
dirtyBlockNextRow		
		lda #levelTilesPerRow*2
		ldx #0
		
		cpx dirtyBlockY1
		beq foundRowStart
		sec
		sbc #levelTilesPerRow 
		inx
		cpx dirtyBlockY1
		beq foundRowStart
		sec
		sbc #levelTilesPerRow
		
foundRowStart
		;.byte 2
		clc
		adc dirtyBlockX1
		tay
		
		ldx dirtyBlockX1
		lda #1
dirtyNextBlock		
		sta render_dirty_blocks,y
		iny
		inx
		cpx dirtyBlockX2
		bne dirtyNextBlock
		
		inc dirtyBlockY1
		lda dirtyBlockY1
		cmp dirtyBlockY2
		bne dirtyBlockNextRow
		
		lda #1
		sta levelScreenDirty
		rts
		

		
		
				

kidX			.byte 70
kidY			.byte 120
kidXOffset		.byte 0
kidFrameIndex 	.byte 0
kidTop			.byte 0


kid_framesL		.byte <kid_22, <kid_23, <kid_24, <kid_25, <kid_26
				.byte <kid_27, <kid_28, <kid_29, <kid_2a, <kid_2b
				.byte <kid_2b

kid_framesH		.byte >kid_22, >kid_23, >kid_24, >kid_25, >kid_26
				.byte >kid_27, >kid_28, >kid_29, >kid_2a, >kid_2b
				.byte >kid_2b

kid_22		ins "images/kid/sprite_22.pic"
kid_23		ins "images/kid/sprite_23.pic"
kid_24		ins "images/kid/sprite_24.pic"
kid_25		ins "images/kid/sprite_25.pic"
kid_26		ins "images/kid/sprite_26.pic"
kid_27		ins "images/kid/sprite_27.pic"
kid_28		ins "images/kid/sprite_28.pic"
kid_29		ins "images/kid/sprite_29.pic"
kid_2a		ins "images/kid/sprite_2a.pic"
kid_2b		ins "images/kid/sprite_2b.pic"
kid_2c		ins "images/kid/sprite_2c.pic"
