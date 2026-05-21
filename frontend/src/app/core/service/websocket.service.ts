import { Injectable, signal } from "@angular/core";
import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";
import { appSettings } from "../../app.config";
import { IGameDTO, IMoveRequest } from "../../shared/models/game.model";
import { connectClient, createClient, disconnectClient, topicSettings } from "./stomp-client.utils";

@Injectable({ providedIn: 'root' })
export class WebSocketService {
    private stompClient: Client | null = null;

    game = signal<IGameDTO | null>(null);
    error = signal<string | null>(null);
    drawOffer = signal<string | null>(null);

    connect(gameId: string, token: string): void {
        disconnectClient(this.stompClient);

        const socket = new SockJS(appSettings.WS_URL);
        this.stompClient = createClient(socket, token);

        connectClient(this.stompClient, gameId, this.game, this.error, this.drawOffer);
    }

    disconnect(): void {
        disconnectClient(this.stompClient);
        this.stompClient = null;
        this.clearState();
    }

    sendMove(gameId: string, move: IMoveRequest): void {
        this.stompClient?.publish({
            destination: `${topicSettings.DESTINATION_PREFIX}${gameId}/move`,
            body: JSON.stringify(move)
        });
    }

    sendAction(gameId: string, action: string): void {
        this.stompClient?.publish({
            destination: `${topicSettings.DESTINATION_PREFIX}${gameId}/${action}`,
            body: ''
        });
    }

    clearState(): void {
        this.game.set(null);
        this.error.set(null);
        this.drawOffer.set(null);
    }

}