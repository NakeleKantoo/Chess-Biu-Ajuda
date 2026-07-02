import { Component, Input } from '@angular/core';
import { RouterLink } from "@angular/router";

@Component({
  selector: 'app-auth-navigate-button',
  imports: [RouterLink],
  templateUrl: './auth-navigate-button.html',
  styleUrl: './auth-navigate-button.scss',
})
export class AuthNavigateButton {
}
