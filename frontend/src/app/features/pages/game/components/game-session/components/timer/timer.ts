import { Component, computed, effect, inject, input, NgZone, OnDestroy, OnInit, signal } from '@angular/core';
import { interval, Subject, takeUntil } from 'rxjs';

@Component({
  selector: 'app-timer',
  imports: [],
  templateUrl: './timer.html',
  styleUrl: './timer.scss',
})
export class Timer implements OnInit, OnDestroy {
  private zone = inject(NgZone); // Injeção moderna (estilo Java Dependency Injection)

  isWhite = input.required<boolean>();
  playerName = input.required<string>();
  time = input.required<number>();
  lastMoveTimestamp = input.required<number>();
  isActive = input.required<boolean>();

  timeRemaining = signal<number>(0);

  displayTime = computed(() => this.formatTime(this.timeRemaining()));

  private destroy$ = new Subject<void>();
  private readonly TICK_MS = 10;

  constructor() {
    effect(() => {
      const active = this.isActive();
      const baseTime = this.time();
      const lastMove = this.lastMoveTimestamp();

      if (!active) {
        this.timeRemaining.set(baseTime);
      } else {
        const delta = Date.now() - lastMove;
        this.timeRemaining.set(Math.max(0, baseTime - delta));
      }
    }, { allowSignalWrites: true });
  }

  ngOnInit() {
    this.zone.runOutsideAngular(() => {
      interval(this.TICK_MS)
        .pipe(takeUntil(this.destroy$))
        .subscribe(() => {
          if (this.isActive() && this.timeRemaining() > 0) {
            this.timeRemaining.update(v => v - this.TICK_MS);
          }
        });
    });
  }

  formatTime(ms: number): string {
    if (ms <= 0) return "00.0";
    const minutes = Math.floor(ms / 60000);
    const seconds = Math.floor((ms % 60000) / 1000);

    if (minutes > 0) {
      return `${minutes}:${seconds.toString().padStart(2, '0')}`;
    }

    const milis = Math.floor((ms % 1000) / 100);
    return `${seconds.toString().padStart(2, '0')}.${milis.toString().padStart(1, '0')}`;
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
