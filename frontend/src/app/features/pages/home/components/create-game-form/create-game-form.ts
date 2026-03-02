import { Component, output, signal } from '@angular/core';
import { IGameConfigDTO, EGameType, EPlayerColorPreference, ETimeControl } from '../../../../../shared/models/create-game.model';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-create-game-form',
  imports: [CommonModule],
  templateUrl: './create-game-form.html',
  styleUrl: './create-game-form.scss',
})
export class CreateGameForm {

  submit = output<IGameConfigDTO>();

  selectedGameType = signal<EGameType>(EGameType.STANDARD);
  selectedTimeControl = signal<ETimeControl>(ETimeControl.RAPID);
  selectedColorPreference = signal<EPlayerColorPreference>(EPlayerColorPreference.RANDOM);

  selectGameType(type: string) {
    this.selectedGameType.set(type as EGameType);
  }

  selectTimeControl(type: string) {
    this.selectedTimeControl.set(type as ETimeControl);
  }

  selectColorPreference(color: string) {
    this.selectedColorPreference.set(color as EPlayerColorPreference);
  }

  onSubmit() {
    const gameConfig: IGameConfigDTO = {
      gameType: this.selectedGameType(),
      timeControl: this.selectedTimeControl(),
      playerColorPreference: this.selectedColorPreference(),
    };
    this.submit.emit(gameConfig);
  }
}
