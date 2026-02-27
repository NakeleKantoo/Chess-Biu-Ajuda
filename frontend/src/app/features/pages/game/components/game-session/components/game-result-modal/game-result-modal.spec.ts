import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GameResultModal } from './game-result-modal';

describe('GameResultModal', () => {
  let component: GameResultModal;
  let fixture: ComponentFixture<GameResultModal>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GameResultModal]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GameResultModal);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
