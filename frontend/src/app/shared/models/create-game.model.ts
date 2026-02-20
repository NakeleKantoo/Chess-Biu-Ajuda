export interface GameConfig {
    gameType: GameType;
    timeControl: TimeControl;
    playerColorPreference: PlayerColorPreference;

    startingColor?: 'WHITE' | 'BLACK';
    whiteTimeRemaining?: number;
    blackTimeRemaining?: number;
    increment?: number;
}

export interface GameType {
    type: 'STANDARD' | 'CHESS960';
}

export interface TimeControl {
    type: 'BULLET' | 'BLITZ' | 'RAPID' | 'CLASSICAL' | 'CUSTOM';
}

export interface PlayerColorPreference {
    color: 'WHITE' | 'BLACK' | 'RANDOM';
}