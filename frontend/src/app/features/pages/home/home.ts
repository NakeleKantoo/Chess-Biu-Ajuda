import { Component, inject } from '@angular/core';
import { AuthService } from '../../../core/auth/auth.service';
import { Router } from '@angular/router';
import { map, take } from 'rxjs';
import { Header } from "../../components/header/header";
import { Footer } from "../../components/footer/footer";
import { ModalWrapper } from "./components/modal-wrapper/modal-wrapper";

@Component({
  selector: 'app-home',
  imports: [Header, Footer, ModalWrapper],
  templateUrl: './home.html',
  styleUrl: './home.scss',
})
export class Home {
  private authService = inject(AuthService);
  private router = inject(Router);

  activeModal: 'create' | 'join' | null = null;

  createGame() {
    console.log('Criando partida');
  }

  joinGame() {
    console.log('Entrando na partida');
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
