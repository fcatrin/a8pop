
memcpy
		ldy #0
		lda memcpyLen+1
		beq memcpyShort
		
memcpyLoopLong		
		lda (memcpySrc),y
		sta (memcpyDst),y
		iny
		bne memcpyLoopLong
		dec memcpyLen+1
		beq memcpyShort
		inc memcpySrc+1
		inc memcpyDst+1
		jmp memcpyLoopLong			
		
memcpyShort
		lda memcpyLen
		beq memcpyEnd

memcpyLoopShort	
		lda (memcpySrc),y
		sta (memcpyDst),y
		iny
		cpy memcpyLen
		bne memcpyLoopShort
		
memcpyEnd
		rts		
