import { Component, computed, input, output, signal } from '@angular/core';

@Component({
  selector: 'app-modal-wrapper',
  imports: [],
  templateUrl: './modal-wrapper.html',
  styleUrl: './modal-wrapper.scss',
})
export class ModalWrapper {
  type = input.required<'join' | 'create' | null>();
  disabled = input<boolean>(false);

  close = output<void>();
  submit = output<void>();

  isClosing = signal<boolean>(false);

  title = computed<string>(() => { return this.getTitle(this.type()); });
  submitButtonText = computed<string>(() => { return this.getButtonText(this.type()); });

  getTitle(type: 'join' | 'create' | null) {
    switch (type) {
      case 'join':
        return 'Entrar na Partida';
      case 'create':
        return 'Criar uma Partida';
      default:
        return '';
    }
  }

  getButtonText(type: 'join' | 'create' | null) {
    switch (type) {
      case 'join':
        return 'Entrar';
      case 'create':
        return 'Criar';
      default:
        return '';
    }
  }

  onClose() {
    this.isClosing.set(true);
    
    setTimeout(() => {
      this.isClosing.set(false);
      this.close.emit();
    }, 200); 
  }

  onSubmit() {
    this.submit.emit();
  }

}
