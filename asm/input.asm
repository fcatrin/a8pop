
inputHandler		
		lda key_input
		cmp key_last
		beq inputHandlerEnd
		sta key_last
		ldx #255
		stx key_input
		
		cmp #key_w
		beq showScreenUp
		cmp #key_s
		beq showScreenDown
		cmp #key_a
		beq showScreenLeft
		cmp #key_d
		beq showScreenRight
		cmp #key_j
		beq moveSpriteRight
		cmp #key_g
		beq moveSpriteLeft
		cmp #key_y
		beq moveSpriteUp
		cmp #key_h
		beq moveSpriteDown
inputHandlerEnd		
		rts
		
showScreenUp
		jsr getScreenUp
		jsr changeScreen
		jmp inputHandlerEnd
		
showScreenDown
		jsr getScreenDown
		jsr changeScreen
		jmp inputHandlerEnd
		
showScreenLeft
		jsr getScreenLeft
		jsr changeScreen
		jmp inputHandlerEnd
		
showScreenRight
		jsr getScreenRight
		jsr changeScreen
		jmp inputHandlerEnd

moveSpriteRight
		ldx kidX
		inx
		cpx #160
		bne moveSpriteX
		ldx #0
		jmp moveSpriteX		
		
moveSpriteLeft
		ldx kidX
		dex
		cpx #$FF
		bne moveSpriteX
		ldx #0
moveSpriteX		
		stx kidX
		jsr kidPreRender
		jmp inputHandlerEnd
		

moveSpriteDown
		ldx kidY
		inx
		cpx #200
		bne moveSpriteY
		ldx #0
		jmp moveSpriteY		
		
moveSpriteUp
		ldx kidY
		dex
		cpx #$FF
		bne moveSpriteY
		ldx #0
moveSpriteY		
		stx kidY
		jsr kidPreRender
		jmp inputHandlerEnd



key_input   = 764
key_w		= 46
key_s		= 62
key_a		= 63
key_d		= 58
key_g		= 61
key_j		= 1
key_y		= 43
key_h		= 57


key_last	.byte 0
