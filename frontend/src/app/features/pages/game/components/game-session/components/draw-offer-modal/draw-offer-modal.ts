import { Component, ElementRef, input, output, viewChild } from '@angular/core';

@Component({
  selector: 'app-draw-offer-modal',
  imports: [],
  templateUrl: './draw-offer-modal.html',
  styleUrl: './draw-offer-modal.scss',
})
export class DrawOfferModal {
  dialogRef = viewChild<ElementRef<HTMLDialogElement>>('dialogElement');

  ngAfterViewInit() {
    // Abre como Modal nativo. Isso BLOQUEIA todo o resto do site automaticamente!
    this.dialogRef()?.nativeElement.showModal();
  }
  
  drawOffer = input.required<string>();

  accept = output<void>();
  decline = output<void>();

  isClosing = false;

  onAccept(): void {
    this.accept.emit();
  }

  onDecline(): void {
    this.isClosing = true;
    
    setTimeout(() => {
      this.decline.emit();
      this.isClosing = false;
    }, 200);
  }

}
