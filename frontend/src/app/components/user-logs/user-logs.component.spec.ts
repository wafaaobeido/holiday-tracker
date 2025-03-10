import { TestBed } from '@angular/core/testing';
import { UserLogsComponent } from './user-logs.component';
import { provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSortModule } from '@angular/material/sort';
import { UserLogService } from '../../services/user-log.service';
import { of } from 'rxjs';

describe('UserLogsComponent', () => {
  let component: UserLogsComponent;
  let userLogService: UserLogService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MatTableModule, MatPaginatorModule, MatSortModule],
      providers: [
        provideRouter([]),
        provideHttpClient(),
        UserLogService,
        UserLogsComponent
      ],
    }).compileComponents();

    component = TestBed.inject(UserLogsComponent);
    userLogService = TestBed.inject(UserLogService);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load user activities on init', () => {
    spyOn(userLogService, 'getUserActivities').and.returnValue(of([]));

    const mockPayload = { preferred_username: 'testUser' };
    const encodedPayload = btoa(JSON.stringify(mockPayload));

    sessionStorage.setItem('token', `header.${encodedPayload}.signature`);

    component.ngOnInit();
    expect(userLogService.getUserActivities).toHaveBeenCalled();
  });
});
