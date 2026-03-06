import { Component, computed, input } from '@angular/core';
import { IMoveResponse } from '../../../../../../../../../shared/models/game.model';
import { SanPadPipe } from './pipes/san-pad.pipe';

interface IMoveHistory {
  firstMove: IMoveResponse;
  secondMove?: IMoveResponse;
}

@Component({
  selector: 'app-move-history',
  imports: [SanPadPipe],
  templateUrl: './move-history.html',
  styleUrl: './move-history.scss',
})
export class MoveHistory {
  moveHistory = input.required<IMoveResponse[]>();

  moves = computed<IMoveHistory[]>(() => {
    const moves: IMoveHistory[] = [];
    const movesLength = this.moveHistory().length;
    
    for (let i = 0; i < movesLength; i += 2) {
      const firstMove = this.moveHistory()[i];
      const secondMove = this.moveHistory()[i + 1];
      if (!secondMove) {
        moves.push({ firstMove });
        break;
      }
      moves.push({ firstMove, secondMove });
    }

    return moves;
  });

}
