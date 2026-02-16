import { Component, EventEmitter, Input, Output } from '@angular/core';
import { IAuthFormData } from '../../models/auth-form.model';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-auth-form',
  imports: [FormsModule],
  templateUrl: './auth-form.html',
  styleUrl: './auth-form.scss',
})
export class AuthForm {
  username: string = '';
  password: string = '';
  confirmPassword: string = '';

  @Input({required: true, alias: 'type'}) type: 'login' | 'register' = 'login';
  @Output() onSubmit = new EventEmitter<IAuthFormData>();

  onFormSubmit() {
    let formData: IAuthFormData = {
      username: this.username,
      password: this.password,
      confirmPassword: this.confirmPassword
    };

    if (this.type === 'login') {
      delete formData.confirmPassword;
    }
    
    this.onSubmit.emit(formData);
  }

}
