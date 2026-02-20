import { Injectable } from "@angular/core";
import { Client, IMessage } from "@stomp/stompjs";
import { BehaviorSubject, Subject } from "rxjs";
import { appSettings } from "../../app.config";
import SockJS from "sockjs-client";
import { IGameDTO, IMoveRequest } from "../../shared/models/game.model";

@Injectable({
    providedIn: 'root'
})
export class WebSocketService {
    private stompClient: Client | null = null;

    private gameSource = new BehaviorSubject<IGameDTO | null>(null);
    public game$ = this.gameSource.asObservable();

    private errorSource = new Subject<string>();
    public errors$ = this.errorSource.asObservable();

    private readonly GAME_TOPIC_PREFIX = '/topic/game/';
    private readonly ERROR_QUEUE = '/user/queue/errors';
    private readonly MOVE_DESTINATION_PREFIX = '/app/game/';

    constructor() { }

    connect(gameId: string, token: string): void {
        const socket = new SockJS(appSettings.WS_URL);
        this.stompClient = new Client({
            webSocketFactory: () => socket,
            connectHeaders: {
                Authorization: `Bearer ${token}`
            },
            reconnectDelay: 5000,
        });

        this.stompClient.onConnect = (frame) => {
            this.stompClient?.subscribe(`${this.GAME_TOPIC_PREFIX}${gameId}`, (message: IMessage) => {
                if (message.body) {
                    const gameData = JSON.parse(message.body);
                    this.gameSource.next(gameData);
                }
            });

            this.stompClient?.subscribe(this.ERROR_QUEUE, (message: IMessage) => {
                this.errorSource.next(message.body);
            });
        };

        this.stompClient.activate();
    }

    sendMove(gameId: string, from: string, to: string, promotion?: string): void {
        const moveDTO: IMoveRequest = { from, to, promotion };
        this.stompClient?.publish({
            destination: `${this.MOVE_DESTINATION_PREFIX}${gameId}/move`,
            body: JSON.stringify(moveDTO)
        });
    }

    sendAction(gameId: string, action: string): void {
        this.stompClient?.publish({
            destination: `${this.MOVE_DESTINATION_PREFIX}${gameId}/${action}`,
            body: JSON.stringify({})
        });
    }

    disconnect(): void {
        this.stompClient?.deactivate();
        this.gameSource.next(null);
    }
}