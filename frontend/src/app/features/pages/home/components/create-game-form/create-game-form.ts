import { Component, EventEmitter, Output } from '@angular/core';
import { IGameConfigDTO, EGameType, EPlayerColorPreference, ETimeControl } from '../../../../../shared/models/create-game.model';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-create-game-form',
  imports: [CommonModule],
  templateUrl: './create-game-form.html',
  styleUrl: './create-game-form.scss',
})
export class CreateGameForm {

  @Output() submit = new EventEmitter<IGameConfigDTO>();

  selectedGameType: EGameType = EGameType.STANDARD;
  selectedTimeControl: ETimeControl = ETimeControl.RAPID;
  selectedColorPreference: EPlayerColorPreference = EPlayerColorPreference.RANDOM;

  selectGameType(type: string) {
    this.selectedGameType = type as EGameType;
  }

  selectTimeControl(type: string) {
    this.selectedTimeControl = type as ETimeControl;
  }

  selectColorPreference(color: string) {
    this.selectedColorPreference = color as EPlayerColorPreference;
  }

  onSubmit() {
    const gameConfig: IGameConfigDTO = {
      gameType: this.selectedGameType,
      timeControl: this.selectedTimeControl,
      playerColorPreference: this.selectedColorPreference,
    };
    this.submit.emit(gameConfig);
  }
}
