import { Component, computed, effect, inject, input, NgZone, OnDestroy, OnInit, signal } from '@angular/core';
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
  
  lastMoveTimestamp = computed<number>(() => this.timerView().lastMoveTimestamp);
  isActive = computed(() => this.timerView().isActive);
  isWhite = computed(() => this.timerView().isWhite)
  playerName = computed(() => this.timerView().playerName);
  displayTime = computed(() => this.formatTime(this.timeRemaining()));

  private destroy$ = new Subject<void>();
  private readonly TICK_MS = 10;

  constructor() {
    effect(() => {
      this.timeRemaining.set(this.timerView().time);
    }, { allowSignalWrites: true });
  }

  ngOnInit() {
    this.zone.runOutsideAngular(() => {
      interval(this.TICK_MS)
        .pipe(takeUntil(this.destroy$))
        .subscribe(() => {
          if (this.isActive() && this.timeRemaining() > 0) {
            const delta = Date.now() - this.lastMoveTimestamp();
            if (delta > 10) {
              this.timeRemaining.update(v => v - this.TICK_MS);
            }
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
