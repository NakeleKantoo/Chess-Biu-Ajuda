import { Component, computed, inject, input, output, signal } from '@angular/core';
import { EGameEndReason, IClockDTO, IGameDTO, IGamePlayersDTO, IMoveRequest } from '../../../../../shared/models/game.model';
import { Board } from "./components/board/board";
import { Timer } from './components/timer/timer';
import { GameResultModal } from "./components/game-result-modal/game-result-modal";
import { Router } from '@angular/router';
import { TimerView } from '../../../../../shared/models/chess-view.model';

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

  endReason = computed<EGameEndReason | null>(() => {
    const endReason = this.gameDTO().endReason;
    return endReason ? endReason : null;
  });

  timersView = computed<TimerView[]>(() => {
    const isTopWhite: boolean = this.isFlipped();
    const isBottomWhite = !isTopWhite;
    const gamePlayers = this.gamePlayers();
    const clock = this.gameDTO().clock;
    const currentPlayer = this.currentPlayer();

    return [
      {
        isWhite: isTopWhite,
        playerName: this.getPlayerName(isTopWhite, gamePlayers),
        time: this.getTimerTime(isTopWhite, clock),
        lastMoveTimestamp: clock.lastMoveTimestamp,
        isActive: this.isTimerActive(clock.isRunning, isTopWhite, currentPlayer)
      },
      {
        isWhite: isBottomWhite,
        playerName: this.getPlayerName(isBottomWhite, gamePlayers),
        time: this.getTimerTime(isBottomWhite, clock),
        lastMoveTimestamp: clock.lastMoveTimestamp,
        isActive: this.isTimerActive(clock.isRunning, isBottomWhite, currentPlayer)
      }
    ];
  });

  executeMove(moveRequest: IMoveRequest): void {
    this.move.emit(moveRequest);
  }

  executeAction(action: string): void {
    this.action.emit(action);
  }

  isTimerActive(isRunning: boolean, isWhite: boolean, currentPlayer: 'white' | 'black'): boolean {
    const player = isWhite ? 'white' : 'black';
    return isRunning && currentPlayer === player;
  }

  getTimerTime(isWhite: boolean, clock: IClockDTO): number {
    return isWhite ? clock.whiteTimeRemaining : clock.blackTimeRemaining;
  }

  getPlayerName(isWhite: boolean, gamePlayers: IGamePlayersDTO | null): string {
    const defaultName = isWhite ? 'White' : 'Black';
    if (!gamePlayers) return defaultName;

    const player = isWhite ? gamePlayers.whitePlayer : gamePlayers.blackPlayer;
    return player.name || defaultName;
  }

  onModalClose(): void {
    this.canModalOpen.set(false);
  }

  goHome(): void {
    this.router.navigate(['/home']);
  }

}