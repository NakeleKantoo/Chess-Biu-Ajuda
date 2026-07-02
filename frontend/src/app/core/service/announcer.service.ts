import { Injectable } from "@angular/core";

@Injectable({ providedIn: 'root' })
export class AriaAnnouncerService {
    announce(message: string, politeness: 'polite' | 'assertive' = 'assertive') {
        const el = document.getElementById('announcer');
        if (!el) return;

        // Limpar primeiro é essencial: o leitor de tela só relê se o conteúdo mudar
        el.textContent = '';
        // Timeout mínimo para garantir que o DOM diff seja percebido
        setTimeout(() => { el.textContent = message; }, 50);
    }
}