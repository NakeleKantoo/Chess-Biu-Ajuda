import { IGamePlayersDTO } from "./game.model";

export class GamePlayers {
    constructor(private dto: IGamePlayersDTO | null) {}
    
    getName(isWhite: boolean): string {
        const defaultName = isWhite ? 'White' : 'Black';
        if (!this.dto) return defaultName;

        const player = isWhite ? this.dto.whitePlayer : this.dto.blackPlayer;
        return player?.name || defaultName;
    }
}