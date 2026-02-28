import { Component, computed, inject, input, output, signal } from '@angular/core';
import { EGameEndReason, IGameDTO, IGamePlayersDTO, IMoveRequest } from '../../../../../shared/models/game.model';
import { Board } from "./components/board/board";
import { Timer } from './components/timer/timer';
import { GameResultModal } from "./components/game-result-modal/game-result-modal";
import { Router } from '@angular/router';

@Component({
  selector: 'app-game-session',
  imports: [Board, Timer, GameResultModal],
  templateUrl: './game-session.html',
  styleUrl: './game-session.scss',
})
export class GameSession {
  private router = inject(Router);

  gameDTO = input.required<IGameDTO>();
  isFlipped = input<boolean>(false);
  gamePlayers = input<IGamePlayersDTO | null>(null);

  move = output<IMoveRequest>();
  action = output<string>();

  canModalOpen = signal<boolean>(true);

  currentPlayer = computed<'white' | 'black'>(() => {
    const fen: string = this.gameDTO().board.fen;
    return fen.split(' ')[1] === 'w' ? 'white' : 'black';
  });

  isRunning = computed<boolean>(() => {
    return this.gameDTO().clock.isRunning;
  });

  endReason = computed<EGameEndReason | null>(() => {
    const endReason = this.gameDTO().endReason;
    return endReason ? endReason : null;
  });

  executeMove(moveRequest: IMoveRequest): void {
    this.move.emit(moveRequest);
  }

  executeAction(action: string): void {
    this.action.emit(action);
  }

  isTimerActive(isWhite: boolean): boolean {
    return this.isRunning() && this.currentPlayer() === (isWhite ? 'white' : 'black');
  }

  getTimerTime(isWhite: boolean): number {
    return isWhite ? this.gameDTO().clock.whiteTimeRemaining : this.gameDTO().clock.blackTimeRemaining;
  }

  getPlayerName(isWhite: boolean): string {
    if (!this.gamePlayers()) return isWhite ? 'White' : 'Black';
    const gamePlayers = this.gamePlayers() as IGamePlayersDTO;
    const player = isWhite ? gamePlayers.whitePlayer : gamePlayers.blackPlayer;
    return player ? player.name : (isWhite ? 'White' : 'Black');
  }

  lastMoveTimestamp = computed<number>(() => {
    return this.gameDTO().clock.lastMoveTimestamp;
  });

  onModalClose() {
    this.canModalOpen.set(false);
  }

  goHome() {
    this.router.navigate(['/home']);
  }

}