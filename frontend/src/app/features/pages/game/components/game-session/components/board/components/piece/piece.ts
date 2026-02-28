import { Component, computed, input } from '@angular/core';

@Component({
  selector: 'app-piece',
  imports: [],
  templateUrl: './piece.html',
  styleUrl: './piece.scss',
})
export class Piece {
  pieceType = input.required<string | null>();

  private readonly assetPath = '/piece';

  icon = computed<string | null>(() => {
    const type = this.pieceType();
    if (!type) return null;
    
    const folder = type === type.toUpperCase() ? 'white' : 'black';
    const prefix = folder === 'white' ? 'w' : 'b';
    const name = type.toLowerCase();

    return `${this.assetPath}/${folder}/${prefix}${name}.png`;
  });

}
