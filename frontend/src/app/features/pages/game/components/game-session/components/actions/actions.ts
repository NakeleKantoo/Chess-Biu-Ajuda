import { Component, input, output } from '@angular/core';
import { MoveHistory } from "./components/move-history/move-history";
import { GameAction, IMoveResponse } from '../../../../../../../shared/models/game.model';

@Component({
  selector: 'app-actions',
  imports: [MoveHistory],
  templateUrl: './actions.html',
  styleUrl: './actions.scss',
})
export class Actions {
  moveHistory = input.required<IMoveResponse[]>();

  action = output<GameAction>();

  onAction(action: GameAction) {
    this.action.emit(action);
  }

}
