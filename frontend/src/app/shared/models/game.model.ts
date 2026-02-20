export interface IGameDTO {
    id: string;

    board: IBoardDTO;
    state: EGameState;
    endReason?: EGameEndReason;

    clock: IClockDTO;
    moveHistory: IMoveResponse[];

    legalMoves: Map<string, string[]>;
}

export interface IBoardDTO {
    fen: string;
}

export interface IClockDTO {
    whiteTimeRemaining: number;
    blackTimeRemaining: number;

    lastMoveTimestamp: number;
    isRunning: boolean;
}

export interface IMoveResponse {
    san: string;
    uci: string;
}

export enum EGameState {
    ACTIVE = 'ACTIVE',
    WHITE_WON = 'WHITE_WON',
    BLACK_WON = 'BLACK_WON',
    DRAW = 'DRAW',
    ABORTED = 'ABORTED'
}

export enum EGameEndReason {
    CHECKMATE = 'CHECKMATE',
    RESIGNATION = 'RESIGNATION',

    TIMEOUT = 'TIMEOUT',
    STALEMATE = 'STALEMATE',
    AGREED_DRAW = 'AGREED_DRAW',
    THREEFOLD_REPETITION = 'THREEFOLD_REPETITION',
    FIFTY_MOVE_RULE = 'FIFTY_MOVE_RULE',
    INSUFFICIENT_MATERIAL = 'INSUFFICIENT_MATERIAL',

    ABORTION = 'ABORTION'
}

export interface IMoveRequest {
    from: string;
    to: string;
    promotion?: string;
}