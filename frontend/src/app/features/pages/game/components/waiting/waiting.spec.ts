import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Waiting } from './waiting';

describe('Waiting', () => {
  let component: Waiting;
  let fixture: ComponentFixture<Waiting>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Waiting]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Waiting);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
