import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-modal-wrapper',
  imports: [],
  templateUrl: './modal-wrapper.html',
  styleUrl: './modal-wrapper.scss',
})
export class ModalWrapper {
  @Input({ required: false }) disabled = false;
  @Input({ required: true }) type: 'join' | 'create' | null = null;
  @Output() close = new EventEmitter<void>();
  @Output() submit = new EventEmitter<void>();

  isClosing = false;

  get title() {
    switch (this.type) {
      case 'join':
        return 'Entrar na Partida';
      case 'create':
        return 'Criar uma Partida';
      default:
        return '';
    }
  }

  get submitButtonText() {
    switch (this.type) {
      case 'join':
        return 'Entrar';
      case 'create':
        return 'Criar';
      default:
        return '';
    }
  }

  onClose() {
    this.isClosing = true;
    
    setTimeout(() => {
      this.close.emit();
      this.isClosing = false;
    }, 200); 
  }

  onSubmit() {
    this.submit.emit();
  }
}
