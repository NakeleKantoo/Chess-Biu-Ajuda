import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { appSettings } from '../../app.config';
import { IGameDTO } from '../../shared/models/game.model';
import { IGameConfigDTO, IJoinGameRequest, IPendingGameDTO } from '../../shared/models/create-game.model';

@Injectable({
    providedIn: 'root'
})
export class GameService {
    private readonly API_URL = `${appSettings.API_URL}/games`;

    constructor(private http: HttpClient) { }

    // POST /api/games
    createGame(config: IGameConfigDTO): Observable<IPendingGameDTO> {
        return this.http.post<IPendingGameDTO>(this.API_URL, config);
    }

    // POST /api/games/join
    joinGame(request: IJoinGameRequest): Observable<IPendingGameDTO> {
        return this.http.post<IPendingGameDTO>(`${this.API_URL}/join`, request);
    }

    // GET /api/games/{gameId}
    getGame(gameId: string): Observable<IGameDTO> {
        return this.http.get<IGameDTO>(`${this.API_URL}/${gameId}`);
    }
}