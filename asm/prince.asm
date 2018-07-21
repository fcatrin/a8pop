
	icl "utilsdef.asm"

SDLSTL = 560
COLOR0 = 708
COLOR1 = 709
COLOR2 = 710

rotationBuffer = 200
rotationBufferNew = 201
rotationBufferOld = 202

maskIndex   = 208
tileIndex   = 210
vramIndex   = 212
spriteIndex = 214

vcount = $D40B


tileMask = $1F
TILE_BLOCK = 20

vramSize = 40*192

dungeon_color0 = $94
dungeon_color1 = $8A
dungeon_color2 = $0C

scanbytes = 40

tileBaseOffset = 3*40
stdTileHeight = 63
tileScanBytes = stdTileHeight*40

levelTilesPerRow	= 10
levelTilesPerScreen = 3*levelTilesPerRow
levelScreens = 24
levelDescriptors = levelTilesPerScreen*levelScreens
levelLinks = 256;

; avoid asm overflow errors
levelTypes   = levelData
levelSpecs   = levelData + 720
levelLinkMap = levelData + 720 + 720
levelLinkLoc = levelData + 720 + 720 + 256
levelMap     = levelData + 720 + 720 + 256 + 256
levelInfo    = levelData + 720 + 720 + 256 + 256 + 96

levelMapTop = 2

		org $800

start
		lda #dungeon_color0		; setup colors
		sta COLOR0
		lda #dungeon_color1
		sta COLOR1
		lda #dungeon_color2
		sta COLOR2

		jsr swapBuffers

		lda #<displayList		; setup display list
		sta SDLSTL
		
		lda #>displayList
		sta SDLSTL+1
		
		; jsr initpmg
		
		jsr genAutoMaskTable
		jsr genRotTables
		
		lda #1
		sta levelNumber
		jsr changeLevel
		
		
		lda #7
		jsr changeScreen

		lda #0
		sta kidFrameIndex

nextFrame
		ldx kidFrameIndex
		inx
		cpx #9
		bne noKidFrameReset
		ldx #0
noKidFrameReset
		stx kidFrameIndex
		jsr kidPreRender		

		jsr inputHandler
		lda levelScreenChanged
		beq noScreenChanged

		lda #0
		sta 559
		jmp waitvsync 
		
noScreenChanged
		lda levelScreenDirty
		beq nextFrame
		lda #0
		sta levelScreenDirty		
		
waitvsync
		lda $D40B
		cmp #130
		bne waitvsync


		lda 20
		sta frame1
		jsr drawBack
		lda 20
		sta frame2
		jsr dirtyClearAll

		jsr swapBuffers
		
		lda levelScreenChanged
		beq noCopyBuffers
		jsr copyBuffers
		lda #0
		sta levelScreenChanged
		
noCopyBuffers
		
		lda #34
		sta 559

		jmp nextFrame	
		
swapBuffers
		lda vramActiveBuffer
		beq vramActiveBufferSecond
		
		lda #<vramBuffer1
		sta vramBuffer
		lda #>vramBuffer1
		sta vramBuffer+1
		
		lda #<vramBuffer2
		sta vramPage1
		sta vramBackBuffer
		lda #>vramBuffer2
		sta vramPage1+1
		sta vramBackBuffer+1
		
		lda #<vramBufferBottom2
		sta vramPage2
		lda #>vramBufferBottom2
		sta vramPage2+1
		
		lda #0
		sta vramActiveBuffer
		rts
vramActiveBufferSecond		
		lda #<vramBuffer2
		sta vramBuffer
		lda #>vramBuffer2
		sta vramBuffer+1
		
		lda #<vramBuffer1
		sta vramPage1
		sta vramBackBuffer
		lda #>vramBuffer1
		sta vramPage1+1
		sta vramBackBuffer+1
		
		lda #<vramBufferBottom1
		sta vramPage2
		lda #>vramBufferBottom1
		sta vramPage2+1
		
		lda #1
		sta vramActiveBuffer
		rts
		
copyBuffers
		lda vramBackBuffer
		sta memcpySrc
		lda vramBackBuffer+1
		sta memcpySrc+1
		
		lda vramBuffer
		sta memcpyDst
		lda vramBuffer+1
		sta memcpyDst+1
		
		lda #<vramSize
		sta memcpyLen
		lda #>vramSize+1
		sta memcpyLen+1
		jmp memcpy
		
		

		icl "level/level.asm"		
		icl "level/render.asm"
		icl "level/prerender.asm"
		icl "level/drawTile.asm"

		icl "sprite.asm"
		icl "mask.asm"
		icl "utils.asm"
		icl "input.asm"
		icl "level/bgdata.asm"
		icl "pmg.asm"

heightLookupL 
		.rept 200
		.byte <([*-heightLookupL]*scanbytes)
		.endr

heightLookupH 
		.rept 200
		.byte >([*-heightLookupH]*scanbytes)
		.endr

spriteWidth			.byte 0
spriteHeight		.byte 0

vramActiveBuffer	.byte 0
vramBuffer			.word 0
vramBackBuffer		.word 0

; x1, x2, y1, y2
boundsKidCurrent	.byte 80, 80, 100, 100 ; safe defaults
boundsKidPrev		.byte 80, 80, 100, 100 
boundsKid			.byte 0, 0, 0, 0

dirtyBlockX1	.byte 0
dirtyBlockX2	.byte 0
dirtyBlockY1	.byte 0
dirtyBlockY2	.byte 0

frame0 .byte 0
frame1 .byte 0
frame2 .byte 0


vramBufferBottom1 = $9000
vramBuffer1 = vramBufferBottom1 - (96*40)
vramBufferBottom2 = $B000
vramBuffer2 = vramBufferBottom2 - (96*40)

		org $A000
displayList
		.byte 112, 112, 112
		.byte $4E
vramPage1
		.word 0
		.rept 95
		.byte $0E
		.endr 
		.byte $4E
vramPage2		
		.word 0
		.rept 95
		.byte $0E
		.endr 
		.byte 65
		.word displayList

		org $2E0
		.word start
		
	