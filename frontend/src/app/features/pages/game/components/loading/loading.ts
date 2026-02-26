import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-loading',
  imports: [RouterLink],
  templateUrl: './loading.html',
  styleUrl: './loading.scss',
})
export class Loading {
  miniTabuleiro = Array.from({ length: 16 }, (_, i) => i);

  isDark(index: number): boolean {
    const row = Math.floor(index / 4);
    const col = index % 4;
    return (row + col) % 2 !== 0;
  }

}
