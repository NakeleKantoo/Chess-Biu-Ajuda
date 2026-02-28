import { Component, computed, input, output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Position } from '../../../../../../../../../shared/models/position.model';

type file = 'a' | 'b' | 'c' | 'd' | 'e' | 'f' | 'g' | 'h';
type rank = '1' | '2' | '3' | '4' | '5' | '6' | '7' | '8';

@Component({
  selector: 'app-square',
  imports: [CommonModule],
  templateUrl: './square.html',
  styleUrl: './square.scss',
})
export class Square {
  position = input.required<Position>();
  pointer = input<boolean>(true);
  isFlipped = input<boolean>(false);

  isMove = input<boolean>(false);
  isCapture = input<boolean>(false);
  isLastMove = input<boolean>(false);
  isCheck = input<boolean>(false);

  squareClicked = output<Position>();

  private readonly fileLabels: file[] = ['a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'] as const;
  private readonly rankLabels: rank[] = ['1', '2', '3', '4', '5', '6', '7', '8'] as const;

  colLabel = computed<file | null>(() => {
    const rowIndex = this.isFlipped() ? 0 : 7;
    if (this.position().row !== rowIndex) return null;
    return this.fileLabels[this.position().col];
  });

  rowLabel = computed<rank | null>(() => {
    const colIndex = this.isFlipped() ? 7 : 0;
    if (this.position().col !== colIndex) return null;
    return this.rankLabels[7 - this.position().row];
  });

  isLightSquare = computed<boolean>(() => {
    const { row, col } = this.position();
    return (row + col) % 2 === 0;
  });

  isDarkSquare = computed<boolean>(() => {
    return !this.isLightSquare();
  });

  onSquareClick(): void {
    return this.squareClicked.emit(this.position());
  };

}
