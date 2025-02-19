import { TestBed } from '@angular/core/testing';
import { HolidaysComponent } from './holiday-list.component';
import { provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSortModule } from '@angular/material/sort';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { HolidayService } from '../../services/holiday.service';
import { of } from 'rxjs';
import { ChangeDetectorRef } from '@angular/core';

describe('HolidayListComponent', () => {
  let component: HolidaysComponent;
  let holidayService: HolidayService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MatTableModule, MatPaginatorModule, MatSortModule, MatSnackBarModule],
      providers: [
        provideRouter([]),
        provideHttpClient(),
        ChangeDetectorRef,
        HolidayService,
        HolidaysComponent
      ],
    }).compileComponents();

    component = TestBed.inject(HolidaysComponent);
    holidayService = TestBed.inject(HolidayService);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should fetch holidays on method call', () => {
    spyOn(holidayService, 'getAllHolidays').and.returnValue(of([]));
    component.fetchAllHolidays();
    expect(holidayService.getAllHolidays).toHaveBeenCalled();
  });
});
