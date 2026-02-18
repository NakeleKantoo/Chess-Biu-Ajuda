import { Component, EventEmitter, Input, Output } from '@angular/core';
import { IAuthFormData } from '../../models/auth-form.model';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-auth-form',
  imports: [FormsModule, RouterLink],
  templateUrl: './auth-form.html',
  styleUrl: './auth-form.scss',
})
export class AuthForm {
  username = '';
  password = '';
  confirmPassword = '';

  @Input({required: true}) type: 'login' | 'register' = 'login';
  @Input() errorMessage: string | null = null;
  @Output() onSubmit = new EventEmitter<IAuthFormData>();

  get textLabel(): string {
    return this.type === 'register' ? 'Crie uma Conta' : 'Entre na sua Conta';
  }

  get descriptionLabel(): string {
    return `Escreva o usuário e a senha para ${this.type === 'register' ? 'criar' : 'entrar na'} sua conta`;
  }

  get buttonLabel(): string {
    return this.type === 'register' ? 'Cadastrar' : 'Entrar';
  }

  onFormSubmit() {
    // Validação básica de frontend
    if (this.type === 'register' && this.password !== this.confirmPassword) {
      this.errorMessage = 'As senhas não coincidem.';
      return;
    }

    const formData: IAuthFormData = {
      username: this.username,
      password: this.password,
      confirmPassword: this.confirmPassword
    };

    if (this.type === 'login') delete formData.confirmPassword;
    
    this.onSubmit.emit(formData);
  }

}
