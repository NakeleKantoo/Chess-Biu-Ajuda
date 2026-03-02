import { IClockDTO } from "./game.model";

export class Clock {
    constructor(private dto: IClockDTO) { }

    get whiteTime(): number { return this.dto.whiteTimeRemaining; }
    get blackTime(): number { return this.dto.blackTimeRemaining; }
    get lastMoveTimestamp(): number { return this.dto.lastMoveTimestamp; }
    get isRunning(): boolean { return this.dto.isRunning; }

    getTime(isWhite: boolean): number {
        return isWhite ? this.whiteTime : this.blackTime;
    }

    isActive(isWhite: boolean, currentPlayer: 'white' | 'black'): boolean {
        const color = isWhite ? 'white' : 'black';
        return this.isRunning && currentPlayer === color;
    }
}