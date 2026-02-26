import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Square } from "../square/square";
import { Position } from '../../../../../../../../../shared/models/position.model';
import { Piece } from "../piece/piece";

@Component({
  selector: 'app-promotion',
  imports: [Square, Piece],
  templateUrl: './promotion.html',
  styleUrl: './promotion.scss',
})
export class Promotion {
  @Input() color: 'white' | 'black' = 'white';

  @Output() promotionSelected = new EventEmitter<string>();
  @Output() promotionCancelled = new EventEmitter<void>();

  private pieces: string[] = ['Q', 'R', 'B', 'N'];

  get promotionPieces(): string[] {
    if (this.color === 'black') {
      return this.pieces.map(piece => piece.toLowerCase());
    }
    return this.pieces;
  }

  getPosition(index: number): Position {
    return new Position(1, index + 1);
  }

  selectPromotion(pieceType: string): void {
    this.promotionSelected.emit(pieceType);
  }

  cancelSelection(): void {
    this.promotionCancelled.emit();
  }
}
