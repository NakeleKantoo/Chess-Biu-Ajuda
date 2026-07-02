import { Component, inject, OnInit, OnDestroy, signal, computed, effect, DestroyRef, SimpleChanges } from '@angular/core';
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
import { AriaAnnouncerService } from '../../../core/service/announcer.service';

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
  drawOffer = this.wsService.drawOffer;
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

  private announcer = inject(AriaAnnouncerService); // serviço abaixo

  constructor() {
    effect(() => {
      const activeGame = this.game();
      const pendingGame = this.pendingGame();
      const playerColor = this.isFlipped() ? 'pretas' : 'brancas';

      if (activeGame) {
        this.announcer.announce('Partida encontrada, tabuleiro disponível. Você está jogando com as ' + playerColor + '.');
        if (this.pendingGame()) this.pendingGame.set(null);
        if (!this.gamePlayers()) this.loadPlayers(activeGame.id);
      } else if (pendingGame) {
        this.announcer.announce('Aguardando oponente. Código da sala exibido.');
      } else {
        this.announcer.announce('Carregando partida...');
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
        return this.gameService.getPendingGame(gameId).pipe(
          catchError(err => {
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
    if (!id) return;
    this.drawOffer.set(null);
    this.wsService.sendMove(id, moveRequest);
  }

  executeAction(action: string): void {
    const id = this.game()?.id;
    if (!id) return;
    this.wsService.sendAction(id, action);
  }

  ngOnDestroy(): void {
    this.wsService.disconnect();
  }
}