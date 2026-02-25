import { Component, EventEmitter, Input, Output, SimpleChanges } from '@angular/core';
import { Square } from "./components/square/square";
import { Piece } from "./components/piece/piece";
import { IBoardDTO, IMoveRequest } from '../../../../../../../shared/models/game.model';
import { Position } from '../../../../../../../shared/models/position.model';
import { Promotion } from "./components/promotion/promotion";

@Component({
  selector: 'app-board',
  imports: [Square, Piece, Promotion],
  templateUrl: './board.html',
  styleUrl: './board.scss',
})
export class Board {
  @Input({required: true}) boardDTO!: IBoardDTO;
  @Input({required: false}) isFlipped: boolean = false;
  @Input({required: false}) lastMove: {from: Position, to: Position} | null = null;

  @Output() move = new EventEmitter<IMoveRequest>();

  boardSquares: string[][] = [];
  currentPlayer: 'white' | 'black' = 'white';

  currentPosition: Position | null = null;
  
  promotionModal: boolean = false;
  promotionTarget: Position | null = null;

  get promotionColumn(): number {
    if (!this.promotionTarget) return 0;
    return this.promotionTarget.col;
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['boardDTO'] && changes['boardDTO'].currentValue !== changes['boardDTO'].previousValue) {
      const fen: string = this.boardDTO.fen;
      this.boardSquares = this.parseFEN(fen);
      this.currentPlayer = fen.split(' ')[1] === 'w' ? 'white' : 'black';

      if (!this.canMove()) {
        this.boardDTO.legalMoves = {};
      }
    }
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
    if (this.promotionModal) {
      this.closePromotionModal();
      this.currentPosition = null;
      return;
    }

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
    const legalMoves: string[] | undefined = this.boardDTO.legalMoves[currentPositionNotation];

    if (!legalMoves || !legalMoves.includes(targetPositionNotation)) {
      this.currentPosition = position;
      return;
    }

    if (this.isPromotion(this.currentPosition, position)) {
      this.promotionModal = true;
      this.promotionTarget = position;
      return;
    }

    this.movePiece(this.currentPosition, position);
  }

  movePiece(from: Position, to: Position, promotion?: string): void {
    const moveRequest: IMoveRequest = {
      from: from.toNotation(),
      to: to.toNotation(),
      promotion: promotion
    };
    this.move.emit(moveRequest);
    this.currentPosition = null;
  }

  isMove(row: number, col: number): boolean {
    if (this.currentPosition === null) return false;
    if (this.promotionTarget !== null) return false;

    const legalMoves = this.boardDTO.legalMoves[this.currentPosition.toNotation()];
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

  isPromotion(from: Position, to: Position): boolean {
    const piece = this.getPieceAtPosition(from);
    if (!piece) return false;
    const isPawn = piece.toLowerCase() === 'p';
    const promotionRank = piece === 'P' ? 0 : 7;
    return isPawn && to.row === promotionRank;
  }

  promote(pieceType: string): void {
    this.movePiece(this.currentPosition!, this.promotionTarget!, pieceType);
    this.closePromotionModal();
  }

  closePromotionModal(): void {
    this.promotionModal = false;
    this.promotionTarget = null;
  }

  canMove(): boolean {
    const isWhite: boolean = this.currentPlayer === 'white';
    const isMyTurn: boolean = (isWhite && !this.isFlipped) || (!isWhite && this.isFlipped);
    return isMyTurn;
  }

}
