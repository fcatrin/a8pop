
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

genRotTables
		ldx #0
nextRot1Table		
		txa
		lsr
		lsr
		sta rot1TableLeft,x
		txa
		and #3
		asl
		asl
		asl
		asl
		asl
		asl		
		sta rot1TableRight,x
		inx
		bne nextRot1Table

nextRot2Table		
		txa
		lsr
		lsr
		lsr
		lsr
		sta rot2TableLeft,x
		txa
		and #$0f
		asl
		asl
		asl
		asl
		sta rot2TableRight,x
		inx
		bne nextRot2Table
		
nextRot3Table		
		txa
		lsr
		lsr
		lsr
		lsr
		lsr
		lsr
		sta rot3TableLeft,x
		txa
		and #$3f
		asl
		asl
		sta rot3TableRight,x
		inx
		bne nextRot3Table		
		rts
		
autoMaskBuffer	.byte 0
autoMaskTable	.rept 256
				.byte 0
				.endr
				
rotTableLeftL	.byte <rot1TableLeft, <rot2TableLeft, <rot3TableLeft
rotTableLeftH	.byte >rot1TableLeft, >rot2TableLeft, >rot3TableLeft
rotTableRightL	.byte <rot1TableRight, <rot2TableRight, <rot3TableRight  
rotTableRightH	.byte >rot1TableRight, >rot2TableRight, >rot3TableRight  
				
rot1TableLeft	.rept 256
				.byte 0
				.endr
rot1TableRight	.rept 256
				.byte 0
				.endr
rot2TableLeft	.rept 256
				.byte 0
				.endr
rot2TableRight	.rept 256
				.byte 0
				.endr
rot3TableLeft	.rept 256
				.byte 0
				.endr
rot3TableRight	.rept 256
				.byte 0
				.endr
				