import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserArchiveComponent } from './user-archive.component';

describe('UserArchiveComponent', () => {
  let component: UserArchiveComponent;
  let fixture: ComponentFixture<UserArchiveComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ UserArchiveComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(UserArchiveComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
