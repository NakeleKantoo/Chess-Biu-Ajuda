import { Component, computed, input, output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Position } from '../../../../../../../../../shared/models/position.model';
import { Piece } from '../piece/piece';
import { SquareView } from '../../../../../../../../../shared/models/chess-view.model';

type file = 'a' | 'b' | 'c' | 'd' | 'e' | 'f' | 'g' | 'h';
type rank = '1' | '2' | '3' | '4' | '5' | '6' | '7' | '8';

@Component({
  selector: 'app-square',
  imports: [CommonModule, Piece],
  templateUrl: './square.html',
  styleUrl: './square.scss',
})
export class Square {
  squareView = input.required<SquareView>();
  pointer = input<boolean>(true);
  isFlipped = input<boolean>(false);

  squareClicked = output<Position>();

  private readonly fileLabels: file[] = ['a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'] as const;
  private readonly rankLabels: rank[] = ['1', '2', '3', '4', '5', '6', '7', '8'] as const;

  colLabel = computed<file | null>(() => {
    const position = this.squareView().position;
    const rowIndex = this.isFlipped() ? 0 : 7;
    
    if (position.row !== rowIndex) return null;
    return this.fileLabels[position.col];
  });

  rowLabel = computed<rank | null>(() => {
    const position = this.squareView().position;
    const colIndex = this.isFlipped() ? 7 : 0;

    if (position.col !== colIndex) return null;
    return this.rankLabels[7 - position.row];
  });

  isLightSquare = computed<boolean>(() => {
    const position = this.squareView().position;
    return (position.row + position.col) % 2 === 0;
  });

  isDarkSquare = computed<boolean>(() => {
    return !this.isLightSquare();
  });

  onSquareClick(): void {
    this.squareClicked.emit(this.squareView().position);
  };

}
