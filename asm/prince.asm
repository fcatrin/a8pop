.bank

.include "utilsdef.asm"

SDLSTL = 560
COLOR0 = 708
COLOR1 = 709
COLOR2 = 710

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

timesTrackIndex = $600
timesTrackAcum  = $602
timesTrackBase  = $606


* = $700

spriteWidth		.byte 0
spriteHeight	.byte 0

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
		jsr changeScreen
		
		jsr kidPreRender

nextFrame
		jsr inputHandler
		lda levelScreenChanged
		beq noScreenChanged

		lda #0
		sta 559
		sta levelScreenChanged
		jsr kidPreRender		; TODO evaluate to do only if needed
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
		lda #34
		sta 559

		jmp nextFrame	

		.include "level/level.asm"		
		.include "level/render.asm"
		.include "level/prerender.asm"
		.include "level/drawTile.asm"

		.include "sprite.asm"
		.include "mask.asm"
		.include "utils.asm"
		.include "input.asm"
		.include "level/bgdata.asm"

heightLookupL 
		.rept 200
		.byte <([*-heightLookupL]*scanbytes)
		.endr

heightLookupH 
		.rept 200
		.byte >([*-heightLookupH]*scanbytes)
		.endr


.bank

vramBack = $9000

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
		
	