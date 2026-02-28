import { Component, computed, input, output } from '@angular/core';
import { Square } from "../square/square";
import { Position } from '../../../../../../../../../shared/models/position.model';
import { Piece, PieceSymbol } from "../piece/piece";

@Component({
  selector: 'app-promotion',
  imports: [Square, Piece],
  templateUrl: './promotion.html',
  styleUrl: './promotion.scss',
})
export class Promotion {
  color = input<'white' | 'black'>('white');

  promotionSelected = output<PieceSymbol>();
  promotionCancelled = output<void>();

  private readonly pieces: PieceSymbol[] = ['Q', 'R', 'B', 'N'] as const;

  promotionPieces = computed<PieceSymbol[]>(() => {
    if (this.color() === 'black') {
      return this.pieces.map(piece => piece.toLowerCase() as PieceSymbol);
    }
    return this.pieces;
  });

  getPosition(index: number): Position {
    return new Position(1, index + 1);
  }

  selectPromotion(pieceSymbol: PieceSymbol): void {
    this.promotionSelected.emit(pieceSymbol);
  }

  cancelSelection(): void {
    this.promotionCancelled.emit();
  }
}
