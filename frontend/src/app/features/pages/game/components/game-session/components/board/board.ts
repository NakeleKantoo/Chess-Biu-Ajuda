import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Square } from "./components/square/square";
import { Piece } from "./components/piece/piece";
import { IBoardDTO } from '../../../../../../../shared/models/game.model';
import { Position } from '../../../../../../../shared/models/position.model';

@Component({
  selector: 'app-board',
  imports: [Square, Piece],
  templateUrl: './board.html',
  styleUrl: './board.scss',
})
export class Board {
  @Input({required: true}) boardDTO!: IBoardDTO;
  @Input({required: false}) isFlipped: boolean = false;
  @Input({required: false}) lastMove: {from: Position, to: Position} | null = null;

  @Output() move = new EventEmitter<{ from: Position, to: Position }>();

  boardSquares: string[][] = [];
  currentPosition: Position | null = null;

  ngOnChanges(): void {
    this.boardSquares = this.parseFEN(this.boardDTO.fen);
  }

  getPosition(row: number, col: number): Position {
    return new Position(row, col);
  }

  getPieceAtPosition(position: Position): string {
    const row = this.boardSquares[position.row];
    if (!row) return '';
    return row[position.col] || '';
  }

  parseFEN(fen: string): string[][] {
    const [position] = fen.split(' ');
    const rows = position.split('/');
    return rows.map(row => {
      const squares: string[] = [];
      for (const char of row) {
        if (isNaN(Number(char))) {
          squares.push(char);
        } else {
          squares.push(...Array(Number(char)).fill(''));
        }
      }
      return squares;
    });
  }

  onClick(position: Position): void {
    if (this.currentPosition === null) {
      this.currentPosition = position;
      return;
    }

    if (position.equals(this.currentPosition)) {
      this.currentPosition = null;
      return;
    }

    const currentPositionNotation: string = this.currentPosition.toNotation();
    const targetPositionNotation: string = position.toNotation();
    const legalMoves: string[] | undefined = this.boardDTO.legalMoves.get(currentPositionNotation);

    if (legalMoves && legalMoves.includes(targetPositionNotation)) {
      this.movePiece(this.currentPosition, position);
    } else {
      this.currentPosition = position;
    }
  }

  movePiece(from: Position, to: Position): void {
    this.move.emit({ from, to });
    this.currentPosition = null;
  }

  isMove(row: number, col: number): boolean {
    if (this.currentPosition === null) return false;

    const legalMoves = this.boardDTO.legalMoves.get(this.currentPosition.toNotation());
    if (!legalMoves) return false;
    
    const position: Position = new Position(row, col);
    return legalMoves.includes(position.toNotation());
  }

  isCapture(row: number, col: number): boolean {
    if (!this.isMove(row, col)) return false;

    const position: Position = new Position(row, col);
    const hasPiece: boolean = !!this.getPieceAtPosition(position);
  
    return hasPiece;
  }

  isLastMove(row: number, col: number): boolean {
    if (this.lastMove === null) return false;
    const { from, to } = this.lastMove;
    const isFrom = from.row === row && from.col === col;
    const isTo = to.row === row && to.col === col;
    return isFrom || isTo;
  }

}
