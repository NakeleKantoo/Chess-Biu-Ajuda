import { ChangeDetectorRef, Component, inject, Input } from '@angular/core';
import { RouterLink } from '@angular/router';
import { IPendingGameDTO } from '../../../../../shared/models/create-game.model';

@Component({
  selector: 'app-waiting',
  imports: [RouterLink],
  templateUrl: './waiting.html',
  styleUrl: './waiting.scss',
})
export class Waiting {
  @Input({ required: true }) pendingGame!: IPendingGameDTO;
  private cdRef = inject(ChangeDetectorRef);

  copyText = 'Copiar Código';
  copyIconClass = 'bi bi-copy pe-2';

  getCode(index: number): string {
    return this.pendingGame.gameCode[index] || '';
  }

  copyToClipboard() {
    const code = this.pendingGame.gameCode;

    if (code) {
      navigator.clipboard.writeText(code).then(() => {
        this.copyText = 'Código Copiado!';
        this.copyIconClass = 'bi bi-check-lg pe-2';
        this.cdRef.detectChanges();
      });
    }
  }

  getTimeControlLabel(): string {
    const timeControl = this.pendingGame.gameConfig.timeControl;
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