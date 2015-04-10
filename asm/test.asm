.bank
* = $600
start	lda #15
		sta 710

		lda #15
		sta 710

		jmp start

.bank
* = $2E0
	.word start
		
	