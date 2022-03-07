import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserAftersignupComponent } from './user-aftersignup.component';

describe('UserAftersignupComponent', () => {
  let component: UserAftersignupComponent;
  let fixture: ComponentFixture<UserAftersignupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ UserAftersignupComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(UserAftersignupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
