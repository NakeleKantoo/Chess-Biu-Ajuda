export interface IGameConfigDTO {
    gameType: EGameType;
    timeControl: ETimeControl;
    playerColorPreference: EPlayerColorPreference;

    startingColor?: 'WHITE' | 'BLACK';
    whiteTimeRemaining?: number;
    blackTimeRemaining?: number;
    increment?: number;
}

export interface IPendingGameDTO {
    gameId: string;
    gameCode: string;
}

export interface IJoinGameRequest {
    gameCode: string;
}

export enum EGameType {
    STANDARD = 'STANDARD',
    CHESS960 = 'CHESS960'
}

export enum ETimeControl {
    BULLET = 'BULLET',
    BLITZ = 'BLITZ',
    RAPID = 'RAPID',
    CLASSICAL = 'CLASSICAL',
    CUSTOM = 'CUSTOM'
}

export enum EPlayerColorPreference {
    WHITE = 'WHITE',
    BLACK = 'BLACK',
    RANDOM = 'RANDOM'
}