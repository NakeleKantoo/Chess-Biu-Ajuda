import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { appSettings } from '../../app.config';
import { IGameDTO, IGamePlayersDTO } from '../../shared/models/game.model';
import { IGameConfigDTO, IJoinGameRequest, IPendingGameDTO } from '../../shared/models/create-game.model';

@Injectable({ providedIn: 'root' })
export class GameService {
    private http = inject(HttpClient);
    private readonly API_URL = `${appSettings.API_URL}/games`;

    createGame = (config: IGameConfigDTO) =>
        this.http.post<IPendingGameDTO>(this.API_URL, config);

    joinGame = (request: IJoinGameRequest) =>
        this.http.post<IPendingGameDTO>(`${this.API_URL}/join`, request);

    getGameSession = (id: string) =>
        this.http.get<IGameDTO>(`${this.API_URL}/${id}`);

    getPendingGame = (id: string) =>
        this.http.get<IPendingGameDTO>(`${this.API_URL}/pending/${id}`);

    getPlayersInGame = (id: string) =>
        this.http.get<IGamePlayersDTO>(`${this.API_URL}/${id}/players`);
}