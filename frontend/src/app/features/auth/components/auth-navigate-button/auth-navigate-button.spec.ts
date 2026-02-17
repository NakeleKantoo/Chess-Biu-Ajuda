import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AuthNavigateButton } from './auth-navigate-button';

describe('AuthNavigateButton', () => {
  let component: AuthNavigateButton;
  let fixture: ComponentFixture<AuthNavigateButton>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AuthNavigateButton]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AuthNavigateButton);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
