import { AfterViewInit, ChangeDetectorRef, Component, computed, ElementRef, inject, output, signal, viewChild } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';

@Component({
  selector: 'app-join-game-form',
  imports: [ReactiveFormsModule],
  templateUrl: './join-game-form.html',
  styleUrl: './join-game-form.scss',
})
export class JoinGameForm implements AfterViewInit {
  readonly inputElement = viewChild.required<ElementRef<HTMLInputElement>>('hiddenInput');

  submit = output<string>();

  code = signal<string>("");
  isFocused = signal<boolean>(false);

  isValid = computed<boolean>(() => { return this.code().length === 6; });

  onInput(event: any) {
    const val = event.target.value.toUpperCase().replace(/[^A-Z0-9]/g, '');
    this.code.set(val);
    event.target.value = val;

    this.onSubmit();
  }

  onSubmit() {
    if (this.isValid()) {
      this.submit.emit(this.code());
    }
  }

  ngAfterViewInit() {
    this.focusInput();
  }

  focusInput() {
    const inputElement = this.inputElement();
    if (inputElement) {
      inputElement.nativeElement.focus();
    }
  }
}
