import { TestBed } from '@angular/core/testing';
import { HolidayService } from './holiday.service';
import { provideHttpClient } from '@angular/common/http';
import { of } from 'rxjs';

describe('HolidayService', () => {
  let service: HolidayService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), HolidayService],
    });
    service = TestBed.inject(HolidayService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should fetch holidays', () => {
    spyOn(service, 'getAllHolidays').and.returnValue(of([]));

    service.getAllHolidays('US').subscribe(holidays => {
      expect(service.getAllHolidays).toHaveBeenCalledWith('US');
      expect(holidays).toEqual([]);
    });
  });
});
