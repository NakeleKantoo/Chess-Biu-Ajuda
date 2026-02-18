import { Component, inject } from '@angular/core';
import { AuthService } from '../../../core/auth/auth.service';
import { Router } from '@angular/router';
import { map, take } from 'rxjs';
import { Header } from "../../components/header/header";
import { Footer } from "../../components/footer/footer";

@Component({
  selector: 'app-home',
  imports: [Header, Footer],
  templateUrl: './home.html',
  styleUrl: './home.scss',
})
export class Home {
  private authService = inject(AuthService);
  private router = inject(Router);

  createGame() {
    if (this.authenticated()) {
      console.log('Creating game...');
    }
  }

  enterGame() {
    if (this.authenticated()) {
      console.log('Entering game...');
    }
  }

  authenticated() : boolean {
    const isAuthenticated = this.authService.isAuthenticated;
    if (!isAuthenticated) {
      this.router.navigate(['/login']);
      return false;
    }
    return true;
  }

}
