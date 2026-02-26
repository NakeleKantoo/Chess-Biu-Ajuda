import { Client, IMessage } from "@stomp/stompjs";
import { IGameDTO } from "../../shared/models/game.model";
import { WritableSignal } from "@angular/core";

export const topicSettings = {
    DESTINATION_PREFIX: '/app/game/',
    TOPIC_PREFIX: '/topic/game/',
    ERROR_TOPIC: '/user/queue/errors',
};

export function createClient(socket: WebSocket, token: string): Client {
    return new Client({
        webSocketFactory: () => socket,
        connectHeaders: { Authorization: `Bearer ${token}` },
        reconnectDelay: 5000,
    });
}

export function onGameUpdate(msg: IMessage, game: WritableSignal<IGameDTO | null>): void {
    if (msg.body) {
        const gameData = JSON.parse(msg.body);
        game.set(gameData);
    }
}

export function onErrorUpdate(msg: IMessage, error: WritableSignal<string | null>): void {
    if (msg.body) {
        const errorMsg = JSON.parse(msg.body).message || 'Erro desconhecido';
        alert(`ERRO: ${errorMsg}`); // Alerta imediato para o usuário
        error.set(errorMsg);
    }
}

export function connectClient(stompClient: Client, gameId: string, game: WritableSignal<IGameDTO | null>, error: WritableSignal<string | null>): void {
    if (!stompClient) return;

    stompClient.onConnect = () => {
        stompClient?.subscribe(`${topicSettings.TOPIC_PREFIX}${gameId}`, (msg) => onGameUpdate(msg, game));
        stompClient?.subscribe(topicSettings.ERROR_TOPIC, (msg) => onErrorUpdate(msg, error));
    };

    stompClient.activate();
}

export function disconnectClient(stompClient: Client | null = null): void {
    if (!stompClient) return;
    stompClient.deactivate();
}