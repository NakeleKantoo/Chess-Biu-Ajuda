import { Component, computed, ElementRef, EventEmitter, input, output, viewChild } from '@angular/core';
import { EGameEndReason, EGameState } from '../../../../../../../shared/models/game.model';

@Component({
  selector: 'app-game-result-modal',
  imports: [],
  templateUrl: './game-result-modal.html',
  styleUrl: './game-result-modal.scss',
})
export class GameResultModal {
  dialogRef = viewChild<ElementRef<HTMLDialogElement>>('dialogElement');

  ngAfterViewInit() {
    // Abre como Modal nativo. Isso BLOQUEIA todo o resto do site automaticamente!
    this.dialogRef()?.nativeElement.showModal();
  }

  isWhite = input.required<boolean>();
  state = input.required<EGameState>();
  endReason = input.required<EGameEndReason>();

  close = output<void>();
  home = output<void>();

  isClosing = false;

  title = computed(() => {
    switch (this.state()) {
      case EGameState.WHITE_WON:
        return this.getTitleMsg(this.isWhite());
      case EGameState.BLACK_WON:
        return this.getTitleMsg(!this.isWhite());
      case EGameState.DRAW:
        return "Empate!";
      case EGameState.ABORTED:
        return "Jogo abortado";
      default:
        return "Resultado do jogo";
    }
  });
  
  subtitle = computed(() => {
    switch (this.endReason()) {
      case EGameEndReason.CHECKMATE:
        return "por xeque-mate";
      case EGameEndReason.RESIGNATION:
        return "por desistência";
      case EGameEndReason.TIMEOUT:
        return "por tempo";
      case EGameEndReason.STALEMATE:
        return "por afogamento";
      case EGameEndReason.AGREED_DRAW:
        return "por acordo";
      case EGameEndReason.THREEFOLD_REPETITION:
        return "por 3 repetições";
      case EGameEndReason.FIFTY_MOVE_RULE:
        return "pela regra dos 50 lances";
      case EGameEndReason.INSUFFICIENT_MATERIAL:
        return "por material insuficiente";
      case EGameEndReason.ABORTION:
        return "que pena!";
      default:
        return "Resultado desconhecido";
    }
  });

  description = computed(() => {
    switch (this.state()) {
      case EGameState.WHITE_WON:
        return this.getDescriptionWinnerMsg(this.isWhite());
      case EGameState.BLACK_WON:
        return this.getDescriptionWinnerMsg(!this.isWhite());
      case EGameState.DRAW:
        return this.getDescriptionDrawMsg();
      case EGameState.ABORTED:
        return "Um mal necessário...";
      default:
        return "";
    }
  });

  isWinner = computed(() => {
    return  (this.state() === EGameState.WHITE_WON && this.isWhite()) ||
            (this.state() === EGameState.BLACK_WON && !this.isWhite());
  });

  isLoser = computed(() => {
    return  (this.state() === EGameState.WHITE_WON && !this.isWhite()) ||
            (this.state() === EGameState.BLACK_WON && this.isWhite());
  });

  isDraw = computed(() => {
    return this.state() === EGameState.DRAW;
  });

  isAborted = computed(() => {
    return this.state() === EGameState.ABORTED;
  });

  private getDescriptionWinnerMsg(won: boolean): string {
    switch (this.endReason()) {
      case EGameEndReason.CHECKMATE:
        return won ? "Impecável! Você mereceu essa vitória!" : "Xeque-mate... acontece até com gênios, né?";
      case EGameEndReason.RESIGNATION:
        return won ? "Seu oponente desistiu! Ele sabia o destino dele." : "Bom, você desistiu. Às vezes é a melhor escolha...";
      case EGameEndReason.TIMEOUT:
        return won ? "Você venceu por tempo! Sorte ou habilidade?" : "Você perdeu por tempo! Era melhor ter levado xeque-mate...";
      default:
        return "Não sei nem o que dizer...";
    }
  }

  private getDescriptionDrawMsg(): string {
    switch (this.endReason()) {
      case EGameEndReason.STALEMATE:
        return "Afogamento! Um triste fim, estava emocionante...";
      case EGameEndReason.AGREED_DRAW:
        return "Equilíbrio perfeito! Como já era esperado...";
      case EGameEndReason.THREEFOLD_REPETITION:
        return "Que chato! Como pode acabar assim?";
      case EGameEndReason.FIFTY_MOVE_RULE:
        return "Vocês não queriam jogar mais, né?";
      case EGameEndReason.INSUFFICIENT_MATERIAL:
        return "Material insuficiente? Não sobra nada!";
      case EGameEndReason.TIMEOUT:
        return "O único vitorioso aqui foi o tempo...";
      default:
        return "Foi um jogo equilibrado! Ou não...";
    }
  }

  private getTitleMsg(won: boolean): string {
    return won ? "Você ganhou!" : "Você perdeu!";
  }

  onClose() {
    this.isClosing = true;
    
    setTimeout(() => {
      this.close.emit();
      this.isClosing = false;
    }, 200);
  }

  goHome() {
    this.home.emit();
  }

}
