import { Component, computed, effect, input, output, signal } from '@angular/core';
import { Square } from "./components/square/square";
import { BoardSymbol, PieceSymbol } from "./components/piece/piece";
import { EBoardState, IBoardDTO, IMoveRequest } from '../../../../../../../shared/models/game.model';
import { Position } from '../../../../../../../shared/models/position.model';
import { Promotion } from "./components/promotion/promotion";
import { SquareView } from '../../../../../../../shared/models/chess-view.model';

@Component({
  selector: 'app-board',
  imports: [Square, Promotion],
  templateUrl: './board.html',
  styleUrl: './board.scss',
})
export class Board {
  boardDTO = input.required<IBoardDTO>();
  isFlipped = input<boolean>(false);

  move = output<IMoveRequest>();

  boardSymbol = computed<BoardSymbol>(() => {
    return this.parseFEN(this.boardDTO().fen);
  });

  boardSquares = computed<SquareView[][]>(() => {
    return this.boardSymbol().map((row, rowIndex) => {
      return row.map((piece, colIndex) => ({
        piece: piece,
        position: this.getPosition(rowIndex, colIndex),
        isSelected: this.isSelected(rowIndex, colIndex),
        isMove: this.isMove(rowIndex, colIndex),
        isCapture: this.isCapture(rowIndex, colIndex),
        isLastMove: this.isLastMove(rowIndex, colIndex),
        isCheck: this.isCheck(rowIndex, colIndex)
      }));
    });
  });

  currentPlayer = computed<'white' | 'black'>(() => {
    const fen: string = this.boardDTO().fen;
    return fen.split(' ')[1] === 'w' ? 'white' : 'black';
  });

  legalMoves = computed<Record<string, string[]>>(() => {
    if (!this.canMove()) {
      return {};
    }
    return this.boardDTO().legalMoves;
  });

  currentPosition = signal<Position | null>(null);
  
  promotionModal = signal<boolean>(false);
  promotionTarget = signal<Position | null>(null);

  accessibilityAnnouncement = computed<string>(() => {
    const board = this.boardDTO();
    const color = this.currentPlayer() === 'white' ? 'Brancas' : 'Pretas';
    
    if (board.lastMove == null) return `Jogo iniciado. ${color} jogam.`;
    
    const inverseColor = this.currentPlayer() === 'white' ? 'Pretas' : 'Brancas';
    const lastMove = board.lastMove.san;
    const check = board.boardState === EBoardState.CHECK ? ', xeque' : '';

    return `Movimento das ${inverseColor}: ${lastMove}, ${color} jogam${check}.`; ;
  });

  get promotionColumn(): number {
    if (!this.promotionTarget()) return 0;
    return this.promotionTarget()?.col || 0;
  }

  getPosition(row: number, col: number): Position {
    return Position.at(row, col);
  }

  getPieceAtPosition(position: Position): PieceSymbol | null {
    const row = this.boardSymbol()[position.row];
    if (!row) return null;
    return row[position.col] || null;
  }

  parseFEN(fen: string): BoardSymbol {
    const [position] = fen.split(' ');
    const rows = position.split('/');
    return rows.map(row => {
      const squares: PieceSymbol[] = [];
      for (const char of row) {
        if (isNaN(Number(char))) {
          squares.push(char as PieceSymbol);
        } else {
          squares.push(...Array(Number(char)).fill(null));
        }
      }
      return squares;
    });
  }

  onClick(position: Position): void {
    if (this.promotionModal()) {
      this.closePromotionModal();
      this.currentPosition.set(null);
      return;
    }

    if (this.currentPosition() === null) {
      this.currentPosition.set(position);
      return;
    }
    const currentPosition = this.currentPosition() as Position;

    if (position === currentPosition) {
      this.currentPosition.set(null);
      return;
    }

    const currentPositionNotation: string = currentPosition.toNotation();
    const targetPositionNotation: string = position.toNotation();
    const legalMoves: string[] | undefined = this.legalMoves()[currentPositionNotation];

    if (!legalMoves || !legalMoves.includes(targetPositionNotation)) {
      this.currentPosition.set(position);
      return;
    }

    if (this.isPromotion(currentPosition, position)) {
      this.promotionModal.set(true);
      this.promotionTarget.set(position);
      return;
    }

    this.movePiece(currentPosition, position);
  }

  movePiece(from: Position, to: Position, promotion?: PieceSymbol): void {
    const moveRequest: IMoveRequest = {
      from: from.toNotation(),
      to: to.toNotation(),
      promotion: promotion
    };
    this.move.emit(moveRequest);
    this.currentPosition.set(null);
  }

  isSelected(row: number, col: number): boolean {
    if (this.currentPosition() == null) return false;
    return this.currentPosition()!.row === row && this.currentPosition()!.col === col;
  }

  isMove(row: number, col: number): boolean {
    if (this.currentPosition() === null) return false;
    if (this.promotionTarget() !== null) return false;

    const legalMoves = this.legalMoves()[this.currentPosition()!.toNotation()];
    if (!legalMoves) return false;
    
    const position: Position = Position.at(row, col);
    return legalMoves.includes(position.toNotation());
  }

  isCapture(row: number, col: number): boolean {
    if (!this.isMove(row, col)) return false;

    const position: Position = Position.at(row, col);
    const hasPiece: boolean = !!this.getPieceAtPosition(position);
  
    return hasPiece;
  }

  isLastMove(row: number, col: number): boolean {
    const lastMove = this.boardDTO().lastMove;
    if (!lastMove) return false;
    const { uci } = lastMove;

    const from = Position.fromNotation(uci.substring(0, 2));
    const to = Position.fromNotation(uci.substring(2, 4));

    const isFrom = from.row === row && from.col === col;
    const isTo = to.row === row && to.col === col;
    
    return isFrom || isTo;
  }

  isCheck(row: number, col: number): boolean {
    const position: Position = Position.at(row, col);
    const kingPosition = this.getPieceAtPosition(position);
    const currentPlayerKing = this.currentPlayer() === 'white' ? 'K' : 'k';
    const isCheck = this.boardDTO().boardState === EBoardState.CHECK;
    return kingPosition === currentPlayerKing && isCheck;
  }

  isPromotion(from: Position, to: Position): boolean {
    const piece = this.getPieceAtPosition(from);
    if (!piece) return false;
    const isPawn = piece.toLowerCase() === 'p';
    const promotionRank = piece === 'P' ? 0 : 7;
    return isPawn && to.row === promotionRank;
  }

  promote(pieceSymbol: PieceSymbol): void {
    this.movePiece(this.currentPosition()!, this.promotionTarget()!, pieceSymbol);
    this.closePromotionModal();
  }

  closePromotionModal(): void {
    this.promotionModal.set(false);
    this.promotionTarget.set(null);
  }

  canMove(): boolean {
    const isWhite: boolean = this.currentPlayer() === 'white';
    const isMyTurn: boolean = (isWhite && !this.isFlipped()) || (!isWhite && this.isFlipped());
    return isMyTurn;
  }

}
