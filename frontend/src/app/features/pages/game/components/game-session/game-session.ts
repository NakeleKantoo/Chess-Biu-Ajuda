import { Component, computed, inject, input, output, signal } from '@angular/core';
import { EGameEndReason, IClockDTO, IGameDTO, IGamePlayersDTO, IMoveRequest } from '../../../../../shared/models/game.model';
import { Board } from "./components/board/board";
import { Timer } from './components/timer/timer';
import { GameResultModal } from "./components/game-result-modal/game-result-modal";
import { Router } from '@angular/router';
import { TimerView } from '../../../../../shared/models/chess-view.model';
import { Clock } from '../../../../../shared/models/clock.model';
import { GamePlayers } from '../../../../../shared/models/game-players.model';

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

  clock = computed(() => new Clock(this.gameDTO().clock));
  players = computed(() => new GamePlayers(this.gamePlayers()));

  timersView = computed<TimerView[]>(() => {
    const clock = this.clock();
    const players = this.players();
    const isFlipped = this.isFlipped();
    const currentPlayer = this.currentPlayer();

    return [
      this.createTimerView(isFlipped, clock, players, currentPlayer),
      this.createTimerView(!isFlipped, clock, players, currentPlayer)
    ];
  });

  private createTimerView(isWhite: boolean, clock: Clock, players: GamePlayers, current: 'white' | 'black'): TimerView {
    return {
      isWhite,
      playerName: players.getName(isWhite),
      time: clock.getTime(isWhite),
      lastMoveTimestamp: clock.lastMoveTimestamp,
      isActive: clock.isActive(isWhite, current)
    };
  }

  executeMove(moveRequest: IMoveRequest): void {
    this.move.emit(moveRequest);
  }

  executeAction(action: string): void {
    this.action.emit(action);
  }

  onModalClose(): void {
    this.canModalOpen.set(false);
  }

  goHome(): void {
    this.router.navigate(['/home']);
  }

}