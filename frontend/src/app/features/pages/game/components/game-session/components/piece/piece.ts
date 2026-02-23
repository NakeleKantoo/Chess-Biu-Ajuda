import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-piece',
  imports: [],
  templateUrl: './piece.html',
  styleUrl: './piece.scss',
})
export class Piece {
  @Input({required: true}) pieceType!: string;

  get icon(): string | null {
    switch (this.pieceType) {
      case 'P':
        return '/piece/white/wp.png';
      case 'R':
        return '/piece/white/wr.png';
      case 'N':
        return '/piece/white/wn.png';
      case 'B':
        return '/piece/white/wb.png';
      case 'Q':
        return '/piece/white/wq.png';
      case 'K':
        return '/piece/white/wk.png';
      case 'p':
        return '/piece/black/bp.png';
      case 'r':
        return '/piece/black/br.png';
      case 'n':
        return '/piece/black/bn.png';
      case 'b':
        return '/piece/black/bb.png';
      case 'q':
        return '/piece/black/bq.png';
      case 'k':
        return '/piece/black/bk.png';
      default:
        return null;
    }
  }
}
