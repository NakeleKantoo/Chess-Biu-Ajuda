import { Component, computed, input } from '@angular/core';

export type PieceSymbol = 'K' | 'Q' | 'R' | 'B' | 'N' | 'P' | 'k' | 'q' | 'r' | 'b' | 'n' | 'p';

export type BoardSymbol = (PieceSymbol | null)[][];

@Component({
  selector: 'app-piece',
  imports: [],
  templateUrl: './piece.html',
  styleUrl: './piece.scss',
})
export class Piece {
  pieceSymbol = input.required<PieceSymbol | null>();

  private readonly assetPath = 'piece';

  icon = computed<string | null>(() => {
    const type = this.pieceSymbol();
    if (!type) return null;
    
    const folder = type === type.toUpperCase() ? 'white' : 'black';
    const prefix = folder === 'white' ? 'w' : 'b';
    const name = type.toLowerCase();

    return `${this.assetPath}/${folder}/${prefix}${name}.png`;
  });

}
