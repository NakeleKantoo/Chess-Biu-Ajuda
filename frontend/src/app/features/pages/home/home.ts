import { Component, inject } from '@angular/core';
import { AuthService } from '../../../core/auth/auth.service';
import { Router } from '@angular/router';
import { Header } from "../../components/header/header";
import { Footer } from "../../components/footer/footer";
import { ModalWrapper } from "./components/modal-wrapper/modal-wrapper";
import { CreateGameForm } from "./components/create-game-form/create-game-form";
import { JoinGameForm } from "./components/join-game-form/join-game-form";
import { GameConfig } from '../../../shared/models/create-game.model';

@Component({
  selector: 'app-home',
  imports: [Header, Footer, ModalWrapper, CreateGameForm, JoinGameForm],
  templateUrl: './home.html',
  styleUrl: './home.scss',
})
export class Home {
  private authService = inject(AuthService);
  private router = inject(Router);

  activeModal: 'create' | 'join' | null = null;

  createGame(gameConfig: GameConfig) {
    console.log(gameConfig);
  }

  joinGame(gameCode: string) {
    console.log(gameCode);
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
