import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { HolidayService } from '../../services/holiday.service';

describe('HolidayService', () => {
  let service: HolidayService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [HolidayService]
    });

    service = TestBed.inject(HolidayService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  it('should fetch holidays', () => {
    service.getAllHolidays('US').subscribe((holidays: string | any[]) => {
      expect(holidays.length).toBe(1);
      expect(holidays[0].name).toBe('New Year');
    });

    const req = httpMock.expectOne('/all/US');
    expect(req.request.method).toBe('GET');
    req.flush([{ name: 'New Year', date: '2024-01-01' }]);
  });

  afterEach(() => {
    httpMock.verify();
  });
});
