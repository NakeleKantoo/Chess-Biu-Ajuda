import { ChangeDetectorRef, Component, Input, NgZone, OnDestroy, OnInit, Output } from '@angular/core';
import { interval, Subject, takeUntil } from 'rxjs';

@Component({
  selector: 'app-timer',
  imports: [],
  templateUrl: './timer.html',
  styleUrl: './timer.scss',
})
export class Timer implements OnInit, OnDestroy {
  @Input({ required: true }) isWhite: boolean = true;
  @Input({ required: true }) playerName: string = '';
  @Input({ required: true }) time: number = 0;
  @Input({ required: true }) isActive: boolean = false;

  private destroy$ = new Subject<void>();
  private precision = 10;
  timeRemaining: number = 0;

  constructor(private cdr: ChangeDetectorRef, private zone: NgZone) {}

  ngOnInit() {
    this.timeRemaining = this.time;
    this.zone.runOutsideAngular(() => {
      interval(this.precision)
        .pipe(takeUntil(this.destroy$))
        .subscribe(() => {
          if (this.isActive && this.timeRemaining > 0) {
            this.timeRemaining -= this.precision;
            
            this.cdr.detectChanges();
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
