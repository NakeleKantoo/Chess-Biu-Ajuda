import { ComponentFixture, TestBed } from '@angular/core/testing';

import { JoinGameForm } from './join-game-form';

describe('JoinGameForm', () => {
  let component: JoinGameForm;
  let fixture: ComponentFixture<JoinGameForm>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [JoinGameForm]
    })
    .compileComponents();

    fixture = TestBed.createComponent(JoinGameForm);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
