import { Component, computed, input, output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Position } from '../../../../../../../../../shared/models/position.model';
import { Piece } from '../piece/piece';
import { SquareView } from '../../../../../../../../../shared/models/chess-view.model';

type file = 'a' | 'b' | 'c' | 'd' | 'e' | 'f' | 'g' | 'h';
type rank = '1' | '2' | '3' | '4' | '5' | '6' | '7' | '8';

const mapSymbolToPieceName: Record<string, string> = {
  'P': 'Peão Branco',
  'R': 'Torre Branca',
  'N': 'Cavalo Branco',
  'B': 'Bispo Branco',
  'Q': 'Rainha Branca',
  'K': 'Rei Branco',
  'p': 'Peão Preto',
  'r': 'Torre Preta',
  'n': 'Cavalo Preto',
  'b': 'Bispo Preto',
  'q': 'Rainha Preta',
  'k': 'Rei Preto'
};

@Component({
  selector: 'app-square',
  imports: [CommonModule, Piece],
  templateUrl: './square.html',
  styleUrl: './square.scss',
})
export class Square {
  squareView = input.required<SquareView>();
  pointer = input<boolean>(true);
  isFlipped = input<boolean>(false);

  squareClicked = output<Position>();

  colLabel = computed<file | null>(() => {
    const position = this.squareView().position;
    const rowIndex = this.isFlipped() ? 0 : 7;
    
    if (position.row !== rowIndex) return null;
    return Position.FILES[position.col] as file;
  });

  rowLabel = computed<rank | null>(() => {
    const position = this.squareView().position;
    const colIndex = this.isFlipped() ? 7 : 0;

    if (position.col !== colIndex) return null;
    return Position.RANKS[position.row] as rank;
  });

  isLightSquare = computed<boolean>(() => {
    const position = this.squareView().position;
    return (position.row + position.col) % 2 === 0;
  });

  isDarkSquare = computed<boolean>(() => {
    return !this.isLightSquare();
  });

  onSquareClick(): void {
    this.squareClicked.emit(this.squareView().position);
  };

  getAriaLabel = computed<string>(() => {
    const position = this.squareView().position.toNotation();
    const piece = this.squareView().piece ? `, ${mapSymbolToPieceName[this.squareView().piece!]}` : 'sem peça';
    const isSelected = this.squareView().isSelected ? ', selecionada' : '';
    const isMove = this.squareView().isMove ? ', movimento' : '';
    const isCapture = this.squareView().isCapture ? ', captura' : '';

    return `Casa ${position}${piece}${isSelected}${isMove}${isCapture}`;
  });

}
