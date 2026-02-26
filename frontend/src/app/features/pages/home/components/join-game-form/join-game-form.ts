import { AfterViewInit, ChangeDetectorRef, Component, ElementRef, EventEmitter, inject, Output, ViewChild } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';

@Component({
  selector: 'app-join-game-form',
  imports: [ReactiveFormsModule],
  templateUrl: './join-game-form.html',
  styleUrl: './join-game-form.scss',
})
export class JoinGameForm implements AfterViewInit {
  @ViewChild('hiddenInput') inputElement!: ElementRef<HTMLInputElement>;

  @Output() submit = new EventEmitter<string>();

  private cdRef = inject(ChangeDetectorRef);
  
  code: string = "";
  isFocused: boolean = false;

  onInput(event: any) {
    const val = event.target.value.toUpperCase().replace(/[^A-Z0-9]/g, '');
    this.code = val;
    event.target.value = val;

    this.onSubmit();
  }

  onSubmit() {
    if (this.isValid) {
      this.submit.emit(this.code);
    }
  }

  get isValid(): boolean {
    return this.code.length === 6;
  }

  ngAfterViewInit() {
    this.focusInput();
    this.cdRef.detectChanges();
  }

  focusInput() {
    if (this.inputElement) {
      this.inputElement.nativeElement.focus();
    }
  }
}
