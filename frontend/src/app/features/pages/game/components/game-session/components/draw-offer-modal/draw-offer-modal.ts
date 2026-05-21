import { Component, input, output } from '@angular/core';

@Component({
  selector: 'app-draw-offer-modal',
  imports: [],
  templateUrl: './draw-offer-modal.html',
  styleUrl: './draw-offer-modal.scss',
})
export class DrawOfferModal {
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
