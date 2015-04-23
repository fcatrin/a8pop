
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
				



autoMaskBuffer	.byte 0
autoMaskTable	.rept 256
				.byte 0
				.endr
			