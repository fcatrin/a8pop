
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
		beq moveSpriteLeft
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

moveSpriteLeft
		ldx kidX
		inx
		cpx #160
		bne moveSpriteLeftOk
		ldx #0
moveSpriteLeftOk		
		stx kidX
		jsr kidPreRender
		jmp inputHandlerEnd

key_input   = 764
key_w		= 46
key_s		= 62
key_a		= 63
key_d		= 58
key_j		= 1

key_last	.byte 0
