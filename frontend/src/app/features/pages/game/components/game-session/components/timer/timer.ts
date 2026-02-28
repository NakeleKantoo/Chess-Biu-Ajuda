import { Component, computed, effect, inject, input, NgZone, OnDestroy, OnInit, signal, untracked } from '@angular/core';
import { interval, Subject, takeUntil } from 'rxjs';
import { TimerView } from '../../../../../../../shared/models/chess-view.model';

@Component({
  selector: 'app-timer',
  imports: [],
  templateUrl: './timer.html',
  styleUrl: './timer.scss',
})
export class Timer implements OnInit, OnDestroy {
  private zone = inject(NgZone);

  timerView = input.required<TimerView>();

  timeRemaining = signal<number>(0);

  isActive = computed(() => this.timerView().isActive);
  isWhite = computed(() => this.timerView().isWhite)
  playerName = computed(() => this.timerView().playerName);
  displayTime = computed(() => this.formatTime(this.timeRemaining()));

  private destroy$ = new Subject<void>();
  private readonly TICK_MS = 10;

  constructor() {
    effect(() => {
      const { time, lastMoveTimestamp, isActive } = this.timerView();

      if (!isActive) {
        this.timeRemaining.set(time);
      } else {
        const delta = Date.now() - lastMoveTimestamp;
        this.timeRemaining.set(Math.max(0, time - delta));
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
