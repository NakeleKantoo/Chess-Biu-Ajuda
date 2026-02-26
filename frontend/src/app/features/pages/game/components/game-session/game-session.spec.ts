import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GameSession } from './game-session';

describe('GameSession', () => {
  let component: GameSession;
  let fixture: ComponentFixture<GameSession>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GameSession]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GameSession);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
