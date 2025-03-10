import { TestBed } from '@angular/core/testing';
import { UserLogService } from './user-log.service';
import { provideHttpClient } from '@angular/common/http';
import { of } from 'rxjs';

describe('UserLogService', () => {
  let service: UserLogService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), UserLogService],
    });
    service = TestBed.inject(UserLogService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should fetch user activities', () => {
    spyOn(service, 'getUserActivities').and.returnValue(of([]));
    service.getUserActivities('testUser').subscribe(activities => {
      expect(service.getUserActivities).toHaveBeenCalledWith('testUser');
      expect(activities).toEqual([]);
    });
  });
});
