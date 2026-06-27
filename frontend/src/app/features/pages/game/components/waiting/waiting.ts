import { Component, computed, input, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ETimeControl, IPendingGameDTO } from '../../../../../shared/models/create-game.model';
import { AuthNavigateButton } from "../../../../auth/components/auth-navigate-button/auth-navigate-button";

@Component({
  selector: 'app-waiting',
  imports: [RouterLink, AuthNavigateButton],
  templateUrl: './waiting.html',
  styleUrl: './waiting.scss',
})
export class Waiting {
  pendingGame = input.required<IPendingGameDTO>();

  timeControl = computed<ETimeControl>(() => { return this.pendingGame().config.timeControl; });
  gameCode = computed<string>(() => { return this.pendingGame().gameCode; });

  copyText = signal<string>('Copiar Código');
  copyIconClass = signal<string>('bi bi-copy pe-2');
  
  timeControlLabel = computed<string>(() => { return this.getTimeControlLabel(this.timeControl()); });

  copyToClipboard(): void {
    const code = this.pendingGame().gameCode;
    if (!code) return;

    navigator.clipboard.writeText(code).then(() => {
      this.copyText.set('Código Copiado!');
      this.copyIconClass.set('bi bi-check-lg pe-2');
    });
  }

  getTimeControlLabel(timeControl: ETimeControl): string {
    switch (timeControl) {
      case 'BULLET':
        return '1 min';
      case 'BLITZ':
        return '3 min';
      case 'RAPID':
        return '10 min';
      case 'CLASSICAL':
        return '30 min';
      case 'CUSTOM':
        return 'Personalizado';
      default:
        return timeControl;
    }
  }

}