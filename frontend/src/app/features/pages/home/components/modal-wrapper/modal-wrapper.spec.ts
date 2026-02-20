import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ModalWrapper } from './modal-wrapper';

describe('ModalWrapper', () => {
  let component: ModalWrapper;
  let fixture: ComponentFixture<ModalWrapper>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ModalWrapper]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ModalWrapper);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
