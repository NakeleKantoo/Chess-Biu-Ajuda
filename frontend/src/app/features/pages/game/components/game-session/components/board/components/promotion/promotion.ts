import { Component, computed, input, output } from '@angular/core';
import { Square } from "../square/square";
import { Position } from '../../../../../../../../../shared/models/position.model';
import { PieceSymbol } from "../piece/piece";
import { SquareView } from '../../../../../../../../../shared/models/chess-view.model';

@Component({
  selector: 'app-promotion',
  imports: [Square],
  templateUrl: './promotion.html',
  styleUrl: './promotion.scss',
})
export class Promotion {
  color = input<'white' | 'black'>('white');

  promotionSelected = output<PieceSymbol>();
  promotionCancelled = output<void>();

  private readonly pieces: PieceSymbol[] = ['Q', 'R', 'B', 'N'] as const;

  promotionSquares = computed<SquareView[]>(() => {
    const symbols = this.color() === 'black' 
      ? this.pieces.map(p => p.toLowerCase() as PieceSymbol) 
      : this.pieces;
    
    return symbols.map((symbol, index) => ({
      piece: symbol,
      position: new Position(1, index + 1),
      isMove: false,
      isCapture: false,
      isLastMove: false,
      isCheck: false
    }));
  });

  selectPromotion(pieceSymbol: PieceSymbol): void {
    this.promotionSelected.emit(pieceSymbol);
  }

  cancelSelection(): void {
    this.promotionCancelled.emit();
  }

}
