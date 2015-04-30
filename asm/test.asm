
levelMap = levelData + $1000

* = $600
	lda levelMap+1,y
	lda levelData
	rts
	
levelData .byte 0

	