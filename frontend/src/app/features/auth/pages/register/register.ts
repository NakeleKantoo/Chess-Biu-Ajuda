import { ChangeDetectorRef, Component, inject } from '@angular/core';
import { AuthForm } from "../../components/auth-form/auth-form";
import { IAuthFormData } from '../../models/auth-form.model';
import { AuthService } from '../../../../core/auth/auth.service';
import { IRegisterRequest } from '../../../../shared/models/auth.model';
import { Router } from '@angular/router';
import { AuthNavigateButton } from "../../components/auth-navigate-button/auth-navigate-button";

@Component({
  selector: 'app-register',
  imports: [AuthForm, AuthNavigateButton],
  templateUrl: './register.html',
  styleUrl: './register.scss',
})
export class Register {
  errorMessage: string | null = null;
  private authService = inject(AuthService);
  private router = inject(Router);
  private cdr = inject(ChangeDetectorRef);

  register(authFormData: IAuthFormData) {
    const registerRequest: IRegisterRequest = {
      username: authFormData.username,
      password: authFormData.password,
      confirmPassword: authFormData.confirmPassword!
    };

    this.authService.register(registerRequest).subscribe({
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
