import { ChangeDetectorRef, Component, inject } from '@angular/core';
import { AuthForm } from "../../components/auth-form/auth-form";
import { IAuthFormData } from '../../models/auth-form.model';
import { ILoginRequest } from '../../../../shared/models/auth.model';
import { AuthService } from '../../../../core/auth/auth.service';
import { Router } from '@angular/router';
import { AuthNavigateButton } from '../../components/auth-navigate-button/auth-navigate-button';

@Component({
  selector: 'app-login',
  imports: [AuthForm, AuthNavigateButton],
  templateUrl: './login.html',
  styleUrl: './login.scss',
})
export class Login {
  errorMessage: string | null = null;
  private authService = inject(AuthService);
  private router = inject(Router);
  private cdr = inject(ChangeDetectorRef);

  login(authFormData: IAuthFormData) {
    const loginRequest: ILoginRequest = {
      username: authFormData.username,
      password: authFormData.password
    };

    this.authService.login(loginRequest).subscribe({
      next: () => {
        this.router.navigate(['/home']);
      },
      error: (err) => {
        if (err.status === 0) {
          this.errorMessage = 'O servidor está desligado.';
        } else if (err.error && err.error.message) {
          this.errorMessage = err.error.message;
        } else {
          this.errorMessage = 'Ocorreu um erro inesperado.';
        }
        this.cdr.detectChanges();
      }
    });
  }
}
