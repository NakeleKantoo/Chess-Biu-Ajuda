import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DrawOfferModal } from './draw-offer-modal';

describe('DrawOfferModal', () => {
  let component: DrawOfferModal;
  let fixture: ComponentFixture<DrawOfferModal>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DrawOfferModal]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DrawOfferModal);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
