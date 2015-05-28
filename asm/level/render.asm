
; draw all pre-rendered tiles (prerender.asm)
; first draw all background tiles (C, B, A)
; then draw all sprites
; finally draw "front background" tiles (D, F)
; skip drawing if render_dirty_blocks,x is not set

drawBack
		ldx #200
		lda #0
clearTimesTrackBase		
		sta timesTrackBase,x
		dex
		bne clearTimesTrackBase
waitVsync1	
		lda vcount
		;bne waitVsync1
		lda #0
		sta 20
		sta timesTrackIndex
		sta timesTrackAcum
		sta timesTrackAcum+1
		
		ldx #0
		ldy #0
drawNextBlock
		lda render_dirty_blocks,x
		beq noDrawA
		
		stx renderBlockNumber
		sty renderBlockOffset
		lda render_piecef,x
		cmp #$83
		bne noShortCut
		jmp noDrawB
noShortCut
		clc
		lda render_piecec_offsetL,y
		adc vramBuffer
		sta vramIndex
		lda render_piecec_offsetH,y
		adc vramBuffer+1
		sta vramIndex+1
		ldy #0
		
		lda render_piecec,x
		jsr drawTileC
		ldy renderBlockOffset
		ldx renderBlockNumber
noDrawC
		lda render_pieceb,x
		beq noDrawB
		clc
		lda render_pieceb_offsetL,y
		adc vramBuffer
		sta vramIndex
		lda render_pieceb_offsetH,y
		adc vramBuffer+1
		sta vramIndex+1
		ldy render_maskb,x
		lda render_pieceb,x
		jsr drawTile
		ldy renderBlockOffset
		ldx renderBlockNumber

noDrawB
		lda render_piecea,x
		beq noDrawA
		clc
		lda render_piecea_offsetL,y
		adc vramBuffer
		sta vramIndex
		lda render_piecea_offsetH,y
		adc vramBuffer+1
		sta vramIndex+1
		ldy render_maska,x
		lda render_piecea,x
		jsr drawTile
		ldy renderBlockOffset
		ldx renderBlockNumber
noDrawA
		iny
		inx
		cpx #levelTilesPerScreen
		beq drawBackEnd
		jmp drawNextBlock
		
drawBackEnd		
		jsr drawKid
	
		ldx #0
		ldy #0	

drawFront
		stx renderBlockNumber
		sty renderBlockOffset
		
		lda render_dirty_blocks,x
		beq noDrawF
		
		lda render_pieced,x
		beq noDrawD

		clc
		lda render_pieced_offsetL,y
		adc vramBuffer
		sta vramIndex
		lda render_pieced_offsetH,y
		adc vramBuffer+1
		sta vramIndex+1
		ldy #0
		lda render_pieced,x
		jsr drawTile
		ldy renderBlockOffset
		ldx renderBlockNumber
		
noDrawD		
		lda render_piecef,x
		beq noDrawF
		clc
		lda render_piecef_offsetL,y
		adc vramBuffer
		sta vramIndex
		lda render_piecef_offsetH,y
		adc vramBuffer+1
		sta vramIndex+1
		ldy render_maskf,x
		lda render_piecef,x
		jsr drawTile
		ldy renderBlockOffset
		ldx renderBlockNumber
noDrawF
		lda #0
		sta render_dirty_blocks,x
		iny
		inx
		cpx #levelTilesPerScreen
		bne drawFront

		ldx #0
renderNextTopTile
		lda render_dirty_blocks+levelTilesPerScreen,x
		beq noRenderTopTile
		lda #0
		sta render_dirty_blocks+levelTilesPerScreen,x
		stx renderBlockNumber
		clc
		lda render_pieced_top_offsetL,x
		adc vramBuffer
		sta vramIndex
		lda render_pieced_top_offsetH,x
		adc vramBuffer+1
		sta vramIndex+1
		ldy #0
		lda render_pieced_top,x
		jsr drawTile
		ldx renderBlockNumber
noRenderTopTile		
		inx
		cpx #levelTilesPerRow
		bne renderNextTopTile
		
		sec
		lda 20
		sta timesTrackAcum
		lda vcount
		sta timesTrackAcum+1
		rts
			
