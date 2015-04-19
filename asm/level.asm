.bank
* = $600
start	lda #<testBuffer
		sta displayList+4

		lda #>testBuffer
		sta displayList+5
		
		lda #<displayList
		sta 560
		
		lda #>displayList
		sta 561

		jmp start


.bank
displayList
		.byte 112, 112, 112, $4E, 0, 0
		.byte $0E, $0E, $0E,$0E
		.byte 65
		.word displayList

testBuffer
		.byte $AA, $55, $FF, $55, $AA

.bank
* = $2E0
	.word start
		
	