import { Component, Input } from '@angular/core';
import { RouterLink } from "@angular/router";

@Component({
  selector: 'app-auth-navigate-button',
  imports: [RouterLink],
  templateUrl: './auth-navigate-button.html',
  styleUrl: './auth-navigate-button.scss',
})
export class AuthNavigateButton {
  @Input({required: true}) type: 'login' | 'register' = 'login';

  get buttonLabel(): string {
    return this.type === 'register' ? 'Entre na sua Conta' : 'Criar uma Conta';
  }

  get navigateLink(): string {
    return this.type === 'register' ? '/login' : '/register';
  }
}
