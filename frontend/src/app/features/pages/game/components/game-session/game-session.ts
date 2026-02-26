import { Component, EventEmitter, Input, Output, SimpleChanges } from '@angular/core';
import { IGameDTO, IGamePlayersDTO, IMoveRequest } from '../../../../../shared/models/game.model';
import { Board } from "./components/board/board";
import { Position } from '../../../../../shared/models/position.model';
import { Timer } from './components/timer/timer';

@Component({
  selector: 'app-game-session',
  imports: [Board, Timer],
  templateUrl: './game-session.html',
  styleUrl: './game-session.scss',
})
export class GameSession {
  @Input({ required: true }) gameDTO!: IGameDTO;
  @Input({ required: false }) isFlipped: boolean = false;
  @Input({ required: false }) gamePlayers: IGamePlayersDTO | null = null;

  @Output() move = new EventEmitter<IMoveRequest>();
  @Output() action = new EventEmitter<string>();

  lastMove: { from: Position, to: Position } | null = null;
  currentPlayer: 'white' | 'black' = 'white';
  isRunning: boolean = false;

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['gameDTO'] && changes['gameDTO'].currentValue !== changes['gameDTO'].previousValue) {
      if (this.gameDTO.endReason) {
        this.finishGame();
      }

      const fen: string = this.gameDTO.board.fen;
      this.currentPlayer = fen.split(' ')[1] === 'w' ? 'white' : 'black';
      this.isRunning = this.gameDTO.clock.isRunning;
    }
  }

  executeMove(moveRequest: IMoveRequest): void {
    const from = Position.fromNotation(moveRequest.from);
    const to = Position.fromNotation(moveRequest.to);
    this.lastMove = { from: from, to: to };
    this.move.emit(moveRequest);
  }

  executeAction(action: string): void {
    this.action.emit(action);
  }

  isTimerActive(isWhite: boolean): boolean {
    return this.isRunning && this.currentPlayer === (isWhite ? 'white' : 'black');
  }

  getTimerTime(isWhite: boolean): number {
    return isWhite ? this.gameDTO.clock.whiteTimeRemaining : this.gameDTO.clock.blackTimeRemaining;
  }

  getPlayerName(isWhite: boolean): string {
    if (!this.gamePlayers) return isWhite ? 'White' : 'Black';
    const player = isWhite ? this.gamePlayers.whitePlayer : this.gamePlayers.blackPlayer;
    return player ? player.name : (isWhite ? 'White' : 'Black');
  }

  get lastMoveTimestamp(): number {
    return this.gameDTO.clock.lastMoveTimestamp;
  }

  finishGame() {
    this.isRunning = false;
  }

}
