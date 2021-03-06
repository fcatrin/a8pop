; DATA

tileWidth 		.byte 0
tileHeight		.byte 0
tileMaskDiff	.byte 0
tileMaskTemp	.byte 0


vramOffsets		.word tileBaseOffset, tileBaseOffset+tileScanBytes, tileBaseOffset+tileScanBytes*2,tileBaseOffset+tileScanBytes*3
vramOffset		.word 0
vramOffsetTemp	.word 0

blockOffset .word 0, 0, 10, 20

piecea	.byte $00, $01, $05, $07, $0a, $01, $01, $0a, $10, $00, $01, $01, $00, $14, $20, $4b
		.byte $01, $00, $00, $01, $00, $97, $00, $01, $00, $a7, $a9, $aa, $ac, $ad, $00, $00
	
pieceb	.byte $00, $02, $06, $08, $0b, $1b, $02, $9e, $1a, $1c, $02, $02, $9e, $4a, $21, $1b
		.byte $4d, $4e, $02, $51, $84, $98, $02, $91, $92, $02, $00, $00, $00, $00, $00, $00

pieceay	.byte $00, $00, $00, $00, $00, $00, $00, $00, $00, $00, $00, $00, $00, $00, $00, $00
		.byte $00, $00, $00, $00, $00, $00, $00, $00, $00, $00, $00, $00, $00, $00, $00, $00

pieceby	.byte $00, $00, $00, $00, $00, $00, $00, $00, $00, $00, $00, $00, $00, $00, $00, $FF
		.byte $00, $00, $00, $00, $00, $00, $00, $00, $00, $00, $00, $00, $00, $00, $00, $00
		
maska	.byte $00, $03, $03, $a4, $03, $03, $03, $03, $03, $00, $03, $03, $00, $03, $ff, $a6 
		.byte $03, $00, $00, $03, $00, $a7, $00, $03, $00, $03, $00, $00, $00, $00, $00, $00
		
maskb	.byte $00, $04, $04, $04, $04, $04, $04, $00, $04, $00, $04, $04, $00, $04, $ff, $04
		.byte $00, $04, $04, $04, $04, $04, $04, $00, $04, $04, $00, $00, $00, $00, $00, $00
		
piecec	.byte $00, $00, $00, $09, $0c, $00, $00, $9f, $00, $1d, $00, $00, $9f, $00, $00, $00
		.byte $4f, $50, $00, $00, $85, $00, $00, $93, $94, $00, $00, $00, $00, $00, $00, $00
		
pieced	.byte $00, $15, $15, $15, $15, $18, $19, $16, $15, $00, $15, $15, $17, $15, $15, $4c
		.byte $15, $15, $15, $15, $86, $15, $15, $15, $15, $15, $ab, $00, $00, $00, $00, $00
		
piecef	.byte $00, $00, $00, $45, $46, $00, $00, $46, $48, $49, $87, $00, $46, $0f, $13, $00
		.byte $00, $00, $00, $00, $83, $00, $00, $00, $00, $a8, $00, $ae, $ae, $ae, $00, $00

maskf	.byte $00, $00, $00, $a8, $ff, $00, $00, $00, $00, $00, $a5, $00, $00, $00, $ff, $00
		.byte $00, $00, $00, $00, $00, $00, $00, $00, $00, $00, $00, $00, $00, $00, $00, $00		

screenData	.byte 0, 0, 0, 1, 1, 1, 1, 1, 20, 20
			.byte 19, 19, 1, 3, 0, 20, 20, 20, 20,20
			.byte 20, 20, 20, 20, 14, 3, 11, 1, 1, 20
screenDataTop
		.rept levelTilesPerRow
		.byte 0
		.endr			

xtestmap .byte 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
		.byte 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
		.byte 14,0, 0, 0, 0, 0, 0, 0, 0, 0

render_piecea 
		.rept levelTilesPerScreen
		.byte 0
		.endr

render_maska 
		.rept levelTilesPerScreen
		.byte 0
		.endr

render_piecea_offsetL 
		.rept levelTilesPerScreen
		.byte 0
		.endr
render_piecea_offsetH 
		.rept levelTilesPerScreen
		.byte 0
		.endr

render_pieceay 
		.rept levelTilesPerScreen
		.byte 0
		.endr

render_pieceb 
		.rept levelTilesPerScreen
		.byte 0
		.endr
render_pieceby 
		.rept levelTilesPerScreen
		.byte 0
		.endr
render_maskb 
		.rept levelTilesPerScreen
		.byte 0
		.endr
render_pieceb_offsetL 
		.rept levelTilesPerScreen
		.byte 0
		.endr
render_pieceb_offsetH 
		.rept levelTilesPerScreen
		.byte 0
		.endr

render_piecec 
		.rept levelTilesPerScreen
		.byte 0
		.endr
render_piecec_offsetL 
		.rept levelTilesPerScreen
		.byte 0
		.endr
render_piecec_offsetH
		.rept levelTilesPerScreen
		.byte 0
		.endr

render_pieced 
		.rept levelTilesPerScreen
		.byte 0
		.endr
render_pieced_offsetL 
		.rept levelTilesPerScreen
		.byte 0
		.endr
render_pieced_offsetH
		.rept levelTilesPerScreen
		.byte 0
		.endr
		
render_piecef 
		.rept levelTilesPerScreen
		.byte 0
		.endr
render_maskf 
		.rept levelTilesPerScreen
		.byte 0
		.endr
render_piecef_offsetL 
		.rept levelTilesPerScreen
		.byte 0
		.endr		
render_piecef_offsetH
		.rept levelTilesPerScreen
		.byte 0
		.endr	

render_pieced_top
		.rept levelTilesPerRow
		.byte 0
		.endr
render_pieced_top_offsetL
		.rept levelTilesPerRow
		.byte 0
		.endr
render_pieced_top_offsetH
		.rept levelTilesPerRow
		.byte 0
		.endr
render_dirty_blocks
		.rept levelTilesPerScreen+levelTilesPerRow
		.byte 0
		.endr	

screenDataLeftB .byte 0, 0, 0		; B pieces from screen to the left to be drawn on first col
screenDataLeftC .byte 0, 0			; C pieces from screen to the left to be drawn on first col (first two rows)
screenDataBottomC 					; C pieces from screen to the bottom + left (1 piece) and screen at bottom (9 pieces)
 		.rept levelTilesPerRow
		.byte 0
		.endr
		
renderBlockNumber		.byte 0
renderBlockOffset		.byte 0
preRenderBlockDst		.byte 0
preRenderBlockSrc		.byte 0
preRenderRow			.byte 0
preRenderCols			.byte 0
preRenderOffsetBlock	.byte 0
preYPos					.byte 0

tmpSaveX			.byte 0
tmpSaveY			.byte 0

tilesL  .byte       0, <tile01, <tile02, <tile03, <tile04, <tile05, <tile06, <tile07
		.byte <tile08, <tile09, <tile0a, <tile0b, <tile0c,       0,       0,       0
		.byte       0,       0,       0, <tile13,       0, <tile15,       0,       0
		.byte       0, <tile19,       0, <tile1b,       0,       0, <tile1e, <tile1f
		.byte <tile20, <tile21, <tile22,       0, <tile24,       0, <tile26,       0
		.byte <tile28,       0, <tile2a, <tile2b, <tile2c, <tile2d,       0,       0
		.byte       0,       0,       0,       0,       0,       0,       0,       0
		.byte       0,       0,       0,       0,       0,       0,       0,       0
		.byte       0,       0,       0,       0,       0, <tile45, <tile46,       0
		.byte       0,       0,       0, <tile4b, <tile4c, <tile4d, <tile4e, <tile4f
		.byte <tile50, <tile51,       0,       0,       0,       0,       0,       0
		.byte       0,       0,       0,       0,       0,       0,       0,       0
		.byte       0,       0,       0,       0,       0,       0,       0,       0
		.byte       0,       0, <tile6a,       0, <tile6c,       0,       0,       0
		.byte       0,       0,       0,       0,       0,       0,       0,       0
		.byte       0,       0,       0,       0,       0,       0,       0,       0
		.byte       0,       0,       0, <tile83, <tile84, <tile85, <tile86, <tile87
		.byte       0,       0,       0,       0,       0,       0,       0,       0
		.byte       0,       0,       0,       0,       0,       0, <tile97,       0
		.byte       0,       0,       0,       0,       0,       0,       0,       0
		.byte <tilea0, <tilea1, <tilea2, <tilea3, <tilea4, <tilea5, <tilea6, <tilea7
		.byte <tilea8,       0,       0,       0,       0,       0,       0,       0
		
tilesH	.byte       0, >tile01, >tile02, >tile03, >tile04, >tile05, >tile06, >tile07
		.byte >tile08, >tile09, >tile0a, >tile0b, >tile0c,       0,       0,       0
		.byte       0,       0,       0, >tile13,       0, >tile15,       0,       0
		.byte       0, >tile19,       0, >tile1b,       0,       0, >tile1e, >tile1f
		.byte >tile20, >tile21, >tile22,       0, >tile24,       0, >tile26,       0
		.byte >tile28,       0, >tile2a, >tile2b, >tile2c, >tile2d,       0,       0
		.byte       0,       0,       0,       0,       0,       0,       0,       0
		.byte       0,       0,       0,       0,       0,       0,       0,       0
		.byte       0,       0,       0,       0,       0, >tile45, >tile46,       0
		.byte       0,       0,       0, >tile4b, >tile4c, >tile4d, >tile4e, >tile4f
		.byte >tile50, >tile51,       0,       0,       0,       0,       0,       0
		.byte       0,       0,       0,       0,       0,       0,       0,       0
		.byte       0,       0,       0,       0,       0,       0,       0,       0
		.byte       0,       0, >tile6a,       0, >tile6c,       0,       0,       0
		.byte       0,       0,       0,       0,       0,       0,       0,       0
		.byte       0,       0,       0,       0,       0,       0,       0,       0
		.byte       0,       0,       0, >tile83, >tile84, >tile85, >tile86, >tile87
		.byte       0,       0,       0,       0,       0,       0,       0,       0
		.byte       0,       0,       0,       0,       0,       0, >tile97,       0
		.byte       0,       0,       0,       0,       0,       0,       0,       0
		.byte >tilea0, >tilea1, >tilea2, >tilea3, >tilea4, >tilea5, >tilea6, >tilea7
		.byte >tilea8,       0,       0,       0,       0,       0,       0,       0


tile01	ins "images/dungeon/tile_01.pic"
tile02	ins "images/dungeon/tile_02.pic"
tile03	ins "images/dungeon/tile_03.pic"
tile04	ins "images/dungeon/tile_04.pic"
tile05	ins "images/dungeon/tile_05.pic"
tile06	ins "images/dungeon/tile_06.pic"
tile07	ins "images/dungeon/tile_07.pic"
tile08	ins "images/dungeon/tile_08.pic"
tile09	ins "images/dungeon/tile_09.pic"
tile0a	ins "images/dungeon/tile_0a.pic"
tile0b	ins "images/dungeon/tile_0b.pic"
tile0c	ins "images/dungeon/tile_0c.pic"
tile13	ins "images/dungeon/tile_13.pic"
tile15	ins "images/dungeon/tile_15.pic"
tile19	ins "images/dungeon/tile_19.pic"
tile1b	ins "images/dungeon/tile_1b.pic"
tile1e	ins "images/dungeon/tile_1e.pic"
tile1f	ins "images/dungeon/tile_1f.pic"
tile20	ins "images/dungeon/tile_20.pic"
tile21	ins "images/dungeon/tile_21.pic"
tile22	ins "images/dungeon/tile_22.pic"
tile24	ins "images/dungeon/tile_24.pic"
tile26	ins "images/dungeon/tile_26.pic"
tile28	ins "images/dungeon/tile_28.pic"
tile2a	ins "images/dungeon/tile_2a.pic"
tile2b	ins "images/dungeon/tile_2b.pic"
tile2c	ins "images/dungeon/tile_2c.pic"
tile2d	ins "images/dungeon/tile_2d.pic"
tile45	ins "images/dungeon/tile_45.pic"
tile46	ins "images/dungeon/tile_46.pic"
tile4b	ins "images/dungeon/tile_4b.pic"
tile4c	ins "images/dungeon/tile_4c.pic"
tile4d	ins "images/dungeon/tile_4d.pic"
tile4e	ins "images/dungeon/tile_4e.pic"
tile4f	ins "images/dungeon/tile_4f.pic"
tile50	ins "images/dungeon/tile_50.pic"
tile51	ins "images/dungeon/tile_51.pic"
tile6a	ins "images/dungeon/tile_6a.pic"
tile6c	ins "images/dungeon/tile_6c.pic"
tile83	ins "images/dungeon/tile_83.pic"
tile84	ins "images/dungeon/tile_84.pic"
tile85	ins "images/dungeon/tile_85.pic"
tile86	ins "images/dungeon/tile_86.pic"
tile87	ins "images/dungeon/tile_87.pic"
tile97	ins "images/dungeon/tile_97.pic"
tilea0	ins "images/dungeon/tile_a0.pic"
tilea1	ins "images/dungeon/tile_a1.pic"
tilea2	ins "images/dungeon/tile_a2.pic"
tilea3	ins "images/dungeon/tile_a3.pic"
tilea4	ins "images/dungeon/tile_a4.pic"
tilea5	ins "images/dungeon/tile_a5.pic"
tilea6	ins "images/dungeon/tile_a6.pic"
tilea7	ins "images/dungeon/tile_a7.pic"
tilea8	ins "images/dungeon/tile_a8.pic"

