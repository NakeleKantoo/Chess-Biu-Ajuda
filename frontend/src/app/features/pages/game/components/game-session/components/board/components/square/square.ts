import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Position } from '../../../../../../../../../shared/models/position.model';

@Component({
  selector: 'app-square',
  imports: [CommonModule],
  templateUrl: './square.html',
  styleUrl: './square.scss',
})
export class Square {
  @Input({required: true}) position!: Position;
  @Input({required: false}) pointer: boolean = true;
  @Input({required: false}) isFlipped: boolean = false;

  @Input({required: false}) isMove: boolean = false;
  @Input({required: false}) isCapture: boolean = false;
  @Input({required: false}) isLastMove: boolean = false;
  
  @Output() squareClicked = new EventEmitter<Position>();

  get colLabel(): string {
    const rowIndex = this.isFlipped ? 0 : 7;
    if (this.position.row !== rowIndex) return '';
    return String.fromCharCode(97 + this.position.col);
  }

  get rowLabel(): string {
    const colIndex = this.isFlipped ? 7 : 0;
    if (this.position.col !== colIndex) return '';
    return (8 - this.position.row).toString();
  }

  isLightSquare(): boolean {
    const { row, col } = this.position;
    return (row + col) % 2 === 0;
  }

  isDarkSquare(): boolean {
    return !this.isLightSquare();
  }

  onSquareClick(): void {
    this.squareClicked.emit(this.position);
  }

}
