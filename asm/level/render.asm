drawAll		
		ldx #0
		ldy #0
drawNextBlock
		stx renderBlockNumber
		sty renderBlockOffset
		lda render_piecef,x
		cmp #$83
		bne noShortCut
		jmp noDrawB
noShortCut
		lda render_piecec_offsetL,y
		sta vramIndex
		lda render_piecec_offsetH,y
		sta vramIndex+1
		ldy #0
		lda render_piecec,x
		jsr drawTileC
		ldy renderBlockOffset
		ldx renderBlockNumber
noDrawC
		lda render_pieceb,x
		beq noDrawB
		lda render_pieceb_offsetL,y
		sta vramIndex
		lda render_pieceb_offsetH,y
		sta vramIndex+1
		ldy render_maskb,x
		lda render_pieceb,x
		jsr drawTile
		ldy renderBlockOffset
		ldx renderBlockNumber

noDrawB
		lda render_piecea,x
		beq noDrawA
		lda render_piecea_offsetL,y
		sta vramIndex
		lda render_piecea_offsetH,y
		sta vramIndex+1
		ldy render_maska,x
		lda render_piecea,x
		jsr drawTile
		ldy renderBlockOffset
		ldx renderBlockNumber

noDrawA
		lda render_pieced,x
		beq noDrawD

		lda render_pieced_offsetL,y
		sta vramIndex
		lda render_pieced_offsetH,y
		sta vramIndex+1
		ldy #0
		lda render_pieced,x
		jsr drawTile
		ldy renderBlockOffset
		ldx renderBlockNumber
		
noDrawD		
		lda render_piecef,x
		beq noDrawF
		lda render_piecef_offsetL,y
		sta vramIndex
		lda render_piecef_offsetH,y
		sta vramIndex+1
		ldy render_maskf,x
		lda render_piecef,x
		jsr drawTile
		ldy renderBlockOffset
		ldx renderBlockNumber
noDrawF		
		iny
		inx
		cpx #levelTilesPerScreen
		beq drawTop
		jmp drawNextBlock
drawTop	
		ldx #0
renderNextTopTile		
		stx renderBlockNumber
		lda render_pieced_top_offsetL,x
		sta vramIndex
		lda render_pieced_top_offsetH,x
		sta vramIndex+1
		ldy #0
		lda render_pieced_top,x
		jsr drawTile
		ldx renderBlockNumber
		inx
		cpx #levelTilesPerRow
		bne renderNextTopTile
		rts
			