.bank

.include "utilsdef.asm"

SDLSTL = 560
COLOR0 = 708
COLOR1 = 709
COLOR2 = 710

maskIndex = 208
tileIndex = 210
vramIndex = 212
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


* = $600

frame0 .byte 0
frame1 .byte 0
frame2 .byte 0

start
		lda #dungeon_color0		; setup colors
		sta COLOR0
		lda #dungeon_color1
		sta COLOR1
		lda #dungeon_color2
		sta COLOR2


		lda #<displayList		; setup display list
		sta SDLSTL
		
		lda #>displayList
		sta SDLSTL+1
		
		jsr genAutoMaskTable
		
		lda #1
		sta levelNumber
		jsr changeLevel
		
		
		lda #0
		sta levelScreen
		
		jsr changeScreen
		jsr preRenderMap
renderAgain
		
		lda 20
		sta frame0
		jsr preRenderOffsets

		lda #0
		sta 559

waitvsync
		lda $D40B
		cmp #130
		bne waitvsync


		lda 20
		sta frame1
		jsr drawAll
		lda 20
		sta frame2
		lda #34
		sta 559

;halt	jmp halt		
		
		lda 20
		adc #90
wait		
		cmp 20
		bne wait
		
		
		ldx levelScreen
		inx
		cpx #levelScreens
		bne renderNextScreen
		ldx #0
renderNextScreen		
		stx levelScreen
		jsr changeScreen
		jsr preRenderMap
		jmp renderAgain
		
		
haltx	jmp haltx		
	

		.include "level/level.asm"		
		.include "level/render.asm"
		.include "level/prerender.asm"
		.include "level/drawTile.asm"

		.include "mask.asm"
		.include "utils.asm"
		.include "level/bgdata.asm"

.bank
*		= $A000
displayList
		.byte 112, 112, 112, 
		.byte $4E
		.word vram
		.rept 95
		.byte $0E
		.endr 
		.byte $4E
		.word vram2
		.rept 95
		.byte $0E
		.endr 
		.byte 65
		.word displayList
		

vram2	= $b000
vram    = vram2 - (96*40)


.bank
* = $2E0
	.word start
		
	