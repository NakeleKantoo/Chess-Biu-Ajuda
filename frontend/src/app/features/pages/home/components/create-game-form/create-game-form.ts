import { Component, EventEmitter, Output } from '@angular/core';
import { GameConfig, GameType, PlayerColorPreference, TimeControl } from '../../../../../shared/models/create-game.model';

@Component({
  selector: 'app-create-game-form',
  imports: [],
  templateUrl: './create-game-form.html',
  styleUrl: './create-game-form.scss',
})
export class CreateGameForm {

  @Output() submit = new EventEmitter<GameConfig>();

  selectedGameType: GameType = { type: 'STANDARD' };
  selectedTimeControl: TimeControl = { type: 'RAPID' };
  selectedColorPreference: PlayerColorPreference = { color: 'RANDOM' };

  selectGameType(type: GameType) {
    this.selectedGameType = type;
  }

  selectTimeControl(type: TimeControl) {
    this.selectedTimeControl = type;
  }

  selectColorPreference(color: PlayerColorPreference) {
    this.selectedColorPreference = color;
  }

  onSubmit() {
    const gameConfig: GameConfig = {
      gameType: this.selectedGameType,
      timeControl: this.selectedTimeControl,
      playerColorPreference: this.selectedColorPreference,
    };
    this.submit.emit(gameConfig);
  }
}
