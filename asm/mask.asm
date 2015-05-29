
genAutoMaskTable 
		ldx #0
		
nextAutoMask		
		txa
		and #$AA
		sta autoMaskBuffer
		clc
		ror
		ora autoMaskBuffer
		sta autoMaskBuffer
		txa
		and #$55
		ora autoMaskBuffer
		sta autoMaskBuffer
		and #$55
		asl
		ora autoMaskBuffer
		eor #255
		sta autoMaskTable,x
		inx
		bne nextAutoMask
		rts

genRot1Table
		ldx #0
nextRot1Table		
		txa
		lsr
		lsr
		sta rot1TableLeft,x
		txa
		and #3
		ror
		ror
		ror
		sta rot1TableRight,x
		inx
		bne nextRot1Table
		rts
		
		
						



autoMaskBuffer	.byte 0
autoMaskTable	.rept 256
				.byte 0
				.endr
rot1TableLeft	.rept 256
				.byte 0
				.endr
rot1TableRight	.rept 256
				.byte 0
				.endr
				