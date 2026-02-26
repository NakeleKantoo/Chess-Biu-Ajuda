import { Component, inject, OnInit, OnDestroy, signal, computed, effect, DestroyRef } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { catchError, of, tap } from 'rxjs';

import { WebSocketService } from '../../../core/service/websocket.service';
import { AuthService } from '../../../core/auth/auth.service';
import { GameService } from '../../../core/service/game.service';
import { IGameDTO, IGamePlayersDTO, IMoveRequest } from '../../../shared/models/game.model';
import { Loading } from './components/loading/loading';
import { Waiting } from './components/waiting/waiting';
import { GameSession } from './components/game-session/game-session';
import { IPendingGameDTO } from '../../../shared/models/create-game.model';

@Component({
  selector: 'app-game',
  standalone: true,
  imports: [Loading, Waiting, GameSession],
  templateUrl: './game.html'
})
export class Game implements OnInit, OnDestroy {
  // 1. Injetamos o DestroyRef para salvar o dia
  private destroyRef = inject(DestroyRef);

  private route = inject(ActivatedRoute);
  private wsService = inject(WebSocketService);
  private gameService = inject(GameService);
  private authService = inject(AuthService);

  game = this.wsService.game;
  pendingGame = signal<IPendingGameDTO | null>(null);
  gamePlayers = signal<IGamePlayersDTO | null>(null);
  currentUser = signal<any>(null);

  isFlipped = computed(() => {
    const players = this.gamePlayers();
    const user = this.currentUser();
    if (!players || !user) return false;

    // O tabuleiro "flipa" (inverte) se eu for o jogador de pretas
    return players.blackPlayer.name === user.username;
  });


  constructor() {
    effect(() => {
      const activeGame = this.game();

      if (activeGame) {
        console.log('Jogo Ativo detectado via Signal:', activeGame);

        // Só limpamos o pending se realmente tivermos um jogo para mostrar
        if (this.pendingGame()) {
          this.pendingGame.set(null);
        }

        // Busca jogadores apenas se necessário
        if (!this.gamePlayers()) {
          this.loadPlayers(activeGame.id);
        }
      }
    });
  }

  ngOnInit(): void {
    const gameId = this.route.snapshot.paramMap.get('id');
    if (gameId) {
      this.loadInitialData(gameId);
      this.loadCurrentUser();
    }
  }

  private loadInitialData(gameId: string) {
    this.gameService.getGameSession(gameId).pipe(
      catchError(() => {
        console.warn('Sessão ativa não encontrada, buscando jogo pendente...');
        return this.gameService.getPendingGame(gameId).pipe(
          catchError(err => {
            console.error('Jogo não encontrado em nenhuma base.', err);
            return of(null);
          })
        );
      }),
      takeUntilDestroyed(this.destroyRef)
    ).subscribe((data) => {
      if (!data) return;

      if ('gameCode' in data) {
        this.wsService.game.set(null); // Garante que não há conflito
        this.pendingGame.set(data as IPendingGameDTO);
      } else if ('board' in data) {
        this.pendingGame.set(null);    // Limpa pendente ANTES de setar o jogo
        this.wsService.game.set(data as IGameDTO);
      }

      // Importante: use o gameId da rota ou o id do objeto
      this.connectWS(gameId);
    });
  }

  private loadCurrentUser() {
    this.authService.validateToken()
      .pipe(takeUntilDestroyed(this.destroyRef)) // 3. E aqui também
      .subscribe(user => this.currentUser.set(user));
  }

  private loadPlayers(gameId: string) {
    this.gameService.getPlayersInGame(gameId)
      .pipe(takeUntilDestroyed(this.destroyRef)) // 3. E aqui também
      .subscribe(p => this.gamePlayers.set(p));
  }

  private connectWS(id: string) {
    const token = this.authService.getToken();
    if (token) this.wsService.connect(id, token);
  }

  executeMove(moveRequest: IMoveRequest): void {
    const id = this.game()?.id;
    if (id) this.wsService.sendMove(id, moveRequest);
  }

  executeAction(action: string): void {
    const id = this.game()?.id;
    if (id) this.wsService.sendAction(id, action);
  }

  ngOnDestroy(): void {
    this.wsService.disconnect();
  }
}