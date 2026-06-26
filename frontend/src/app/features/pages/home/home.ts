import { Component, ElementRef, inject, ViewChild } from '@angular/core';
import { AuthService } from '../../../core/auth/auth.service';
import { Router } from '@angular/router';
import { Header } from "../../components/header/header";
import { Footer } from "../../components/footer/footer";
import { ModalWrapper } from "./components/modal-wrapper/modal-wrapper";
import { CreateGameForm } from "./components/create-game-form/create-game-form";
import { JoinGameForm } from "./components/join-game-form/join-game-form";
import { GameService } from '../../../core/service/game.service';
import { IGameConfigDTO, IPendingGameDTO } from '../../../shared/models/create-game.model';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-home',
  imports: [Header, Footer, ModalWrapper, CreateGameForm, JoinGameForm],
  templateUrl: './home.html',
  styleUrl: './home.scss',
})
export class Home {
  private authService = inject(AuthService);
  private gameService = inject(GameService);
  private router = inject(Router);

  activeModal: 'create' | 'join' | null = null;

  isAuthenticated: boolean = this.authService.isAuthenticated;

  @ViewChild('pageTitle')
  pageTitle!: ElementRef<HTMLHeadingElement>;

  ngAfterViewInit() {
    this.pageTitle.nativeElement.focus();
  }

  createGame(gameConfig: IGameConfigDTO) {
    const pendingGameDTO: Observable<IPendingGameDTO> = this.gameService.createGame(gameConfig);
    this.subscribeToGame(pendingGameDTO);
  }

  joinGame(gameCode: string) {
    const pendingGameDTO: Observable<IPendingGameDTO> = this.gameService.joinGame({ gameCode });
    this.subscribeToGame(pendingGameDTO);
  }

  subscribeToGame(pendingGameDTO: Observable<IPendingGameDTO>) {
    pendingGameDTO.subscribe({
      next: (pendingGame) => {
        this.router.navigate(['/game', pendingGame.gameId]);
      },
      error: (err) => {
        alert('Ocorreu um erro ao entrar no jogo. Verifique o código e tente novamente.');
      }
    });
  }

  authenticated() : boolean {
    const isAuthenticated = this.authService.isAuthenticated;
    if (!isAuthenticated) {
      this.router.navigate(['/login']);
      return false;
    }
    return true;
  }

  openCreateModal() {
    if (this.authenticated()) {
      this.activeModal = 'create';
    }
  }

  openJoinModal() {
    if (this.authenticated()) {
      this.activeModal = 'join';
    }
  }

  closeModals() {
    this.activeModal = null;
  }

}
