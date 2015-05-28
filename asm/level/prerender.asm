
; Pre Render Map
; Create lookup tables for each tile pieces with screen offset

preRenderMap
		lda #0
		sta preRenderBlockDst

		ldx #6					; start in third row
		stx preRenderRow

preRenderNextRow		
		lda blockOffset,x
		sta preRenderBlockSrc

		lda #10
		sta preRenderCols

preRenderNextBlock
		ldx preRenderBlockSrc		; load block number from map
		lda screenData,x
		and #tileMask
		tay
		ldx preRenderBlockDst		; y = block id, x = tile position
		lda piecea,y				; write piecea, maska for this block
		sta render_piecea,x
		lda pieceay,y
		sta render_pieceay,x
		lda maska,y
		sta render_maska,x
		lda pieced,y
		sta render_pieced,x
		lda piecef,y
		sta render_piecef,x
		lda maskf,y
		sta render_maskf,x
		
		lda preRenderCols			; last column dont need pieceb and piecec (all pieces to the left)
		cmp #1
		beq skipPieceLeft
		
		lda pieceb,y				; write pieceb, maskb for this block
		sta render_pieceb+1,x
		lda maskb,y
		sta render_maskb+1,x
		lda pieceby,y
		sta render_pieceby+1,x
		
		lda preRenderBlockDst	    ; write piecec for this block only in rows 2 & 3
		cmp #20
		bpl skipPieceLeft
		lda piecec,y
		sta render_piecec+11,x
skipPieceLeft		
		inc preRenderBlockSrc
		inc preRenderBlockDst
		dec preRenderCols
		bne preRenderNextBlock
preRenderNextLine		
		ldx preRenderRow
		dex
		dex
		stx preRenderRow
		bne preRenderNextRow
		

; now go for the top row
preNextTopTile
		lda screenDataTop,x
		and #$1F
		tay
		lda pieced,y
		sta render_pieced_top,x
		inx
		cpx #levelTilesPerRow
		bne preNextTopTile
		
; now go for piece b from last column of left screen
		lda screenDataLeftB+2
		and #$1F
		tay
		lda pieceb,y
		sta render_pieceb  + 0
		lda maskb,y
		sta render_maskb   + 0
		lda pieceby,y
		sta render_pieceby + 0
		
		lda screenDataLeftB+1
		and #$1F
		tay
		lda pieceb,y
		sta render_pieceb  + levelTilesPerRow
		lda maskb,y
		sta render_maskb   + levelTilesPerRow
		lda pieceby,y
		sta render_pieceby + levelTilesPerRow
		
		lda screenDataLeftB+0
		and #$1F
		tay
		lda pieceb,y
		sta render_pieceb  + levelTilesPerRow*2
		lda maskb,y
		sta render_maskb   + levelTilesPerRow*2
		lda pieceby,y
		sta render_pieceby + levelTilesPerRow*2
		
; now go for piece c from left screen(s)		

		lda screenDataLeftC+1
		and #$1F
		tay
		lda piecec,y
		sta render_piecec+levelTilesPerRow
		
		lda screenDataLeftC+0
		and #$1F
		tay
		lda piecec,y
		sta render_piecec+levelTilesPerRow*2
		
		ldx #levelTilesPerRow-1
preCopyBottomTiles		
		lda screenDataBottomC,x
		and #$1F
		tay
		lda piecec,y
		sta render_piecec,x
		dex
		bpl preCopyBottomTiles

		
; remove unused blocks
		ldx #0
removeUnusedNext		
		lda render_piecef,x
		cmp #$83
		bne noRemoveBlock
		lda #0
		sta render_piecea,x
		sta render_pieceb,x
		sta render_piecec,x
		jmp removeNext
noRemoveBlock
		lda render_piecea,x
		beq removeNext
		lda #0
		sta render_piecec,x
		;jmp removeNext
removeNext
		inx
		cpx #levelTilesPerScreen
		bne removeUnusedNext				
		rts

preRenderOffsets
		lda #0
		sta preRenderOffsetBlock
		sta preRenderBlockDst
		
		ldx #6					; start in third row
		
preRenderOffsetRow		
		stx preRenderRow
		lda #10
		sta preRenderCols

		lda vramOffsets,x		; set vramOffset for row on x
		sta vramOffset
		lda vramOffsets+1,x
		sta vramOffset+1

preRenderOffsetNextCol
		lda vramOffset
		sta vramOffsetTemp
		lda vramOffset+1
		sta vramOffsetTemp+1

		ldx preRenderBlockDst
		lda render_pieced,x
		ldy #0
		jsr getBlockVramOffset
		ldx preRenderOffsetBlock
		sta render_pieced_offsetH,x
		tya
		sta render_pieced_offsetL,x
		
		ldx preRenderBlockDst
		lda render_pieceb,x
		ldy render_pieceby,x
		jsr getBlockVramOffset
		ldx preRenderOffsetBlock
		sta render_pieceb_offsetH,x
		tya
		sta render_pieceb_offsetL,x

		ldx preRenderBlockDst
		lda render_piecec,x
		ldy #0
		jsr getBlockVramOffset
		ldx preRenderOffsetBlock
		sta render_piecec_offsetH,x
		tya
		sta render_piecec_offsetL,x
		
		sec								; offsetF = offsetA = offset - 3 scanlines
		lda vramOffset
		sbc #3*scanbytes
		sta vramOffsetTemp
		lda vramOffset+1
		sbc #0
		sta vramOffsetTemp+1

		ldx preRenderBlockDst
		lda render_piecea,x
		ldy render_pieceay,x
		jsr getBlockVramOffset
		ldx preRenderOffsetBlock
		sta render_piecea_offsetH,x
		tya
		sta render_piecea_offsetL,x
		ldx preRenderBlockDst
		lda render_piecef,x
		ldy #0
		jsr getBlockVramOffset
		ldx preRenderOffsetBlock
		sta render_piecef_offsetH,x
		tya
		sta render_piecef_offsetL,x

		clc								; offsetB = offset + 4 bytes (next tile col)
		lda vramOffset					; offsetC = offsetB (for now)
		adc #4
		sta vramOffset
		bcc vramOffsetNoOverflow
		inc vramOffset+1
vramOffsetNoOverflow		
		
		inc preRenderBlockDst
		inc preRenderOffsetBlock
		
		dec preRenderCols
		beq preRenderOffsetNextRow
		jmp preRenderOffsetNextCol
preRenderOffsetNextRow		
		ldx preRenderRow
		dex
		dex
		beq preRenderOffsetEnd
		jmp preRenderOffsetRow
preRenderOffsetEnd

; calc offsets for first row.  Constant for now
		lda vramOffsets,x		; set vramOffset for row on x
		sta vramOffset
		lda vramOffsets+1,x
		sta vramOffset+1
		ldx #0
nextTopOffset
		lda vramOffset+1		
		sta render_pieced_top_offsetH,x
		lda vramOffset
		sta render_pieced_top_offsetL,x
		clc
		adc #4
		sta vramOffset
		bcc noOverflowTop
		inc vramOffset+1
noOverflowTop
		inx
		cpx #levelTilesPerRow
		bne nextTopOffset
		rts

; return first position in screen for tile in A
; tileOffset Y = LSB, A = MSB
getBlockVramOffset
		tax
		sty preYPos
		lda tilesL,x
		sta tileIndex
		lda tilesH,x
		sta tileIndex+1
		
		ldy #1
		lda (tileIndex),y		; get tile height
		sec
		sbc preYPos
		tax
		sec
		lda vramOffsetTemp
		sbc heightLookupL,x
		tay
		lda vramOffsetTemp+1
		sbc heightLookupH,x
		rts