import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { UserLogService } from '../../services/user-log.service';

describe('UserActivityService', () => {
  let service: UserLogService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [UserLogService]
    });

    service = TestBed.inject(UserLogService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  it('should fetch user activities', () => {
    service.getUserActivities('123').subscribe((activities) => {
      expect(activities.length).toBe(1);
      expect(activities[0].content).toBe('User logged in');
    });

    const req = httpMock.expectOne('/user-activities/123');
    expect(req.request.method).toBe('GET');
    req.flush([{ userName: '123', content: 'User logged in', timestamp: '2024-01-01T12:00:00Z' }]);
  });

  afterEach(() => {
    httpMock.verify();
  });
});
