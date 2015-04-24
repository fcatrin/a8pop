
; Generic memcpy optimized for speed. No overlapping 
; fcatrin@gmail.com
;
; Copy operation is divided in two parts.  N is the total length 
; * memcpyLong  for N / 256 blocks
; * memcpyShort for N % 256 remaining bytes

memcpy
		ldy #0
		ldx memcpyLen+1
		beq memcpyShort		; we need only the short version for <1 pages
		
memcpyLoopLong				; copy X pages
		lda (memcpySrc),y
		sta (memcpyDst),y
		iny
		bne memcpyLoopLong
		dex
		beq memcpyShort
		inc memcpySrc+1
		inc memcpyDst+1
		jmp memcpyLoopLong			
		
memcpyShort					; short copy if N % 256 != 0
		ldx memcpyLen
		beq memcpyEnd

memcpyLoopShort				; copy X bytes
		lda (memcpySrc),y
		sta (memcpyDst),y
		iny
		dex
		bne memcpyLoopShort
		
memcpyEnd
		rts		
