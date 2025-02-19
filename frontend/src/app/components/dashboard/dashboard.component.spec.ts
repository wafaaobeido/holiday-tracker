import { TestBed } from '@angular/core/testing';
import { DashboardComponent } from './dashboard.component';
import { provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';

describe('DashboardComponent', () => {
  let component: DashboardComponent;
  let authService: AuthService;
  let router: Router;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [
        provideRouter([]),
        provideHttpClient(),
        AuthService,
        DashboardComponent
      ],
    }).compileComponents();

    component = TestBed.inject(DashboardComponent);
    authService = TestBed.inject(AuthService);
    router = TestBed.inject(Router);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should redirect to login if no token is found', () => {
    spyOn(router, 'navigate');
    sessionStorage.removeItem('token');
    component.ngOnInit();
    expect(router.navigate).toHaveBeenCalledWith(['/login']);
  });

  it('should set username from token if token exists', () => {
    const mockTokenPayload = { preferred_username: 'testUser' };
    const encodedPayload = btoa(JSON.stringify(mockTokenPayload));
    sessionStorage.setItem('token', `header.${encodedPayload}.signature`);
    component.ngOnInit();
    expect(component.username).toEqual('testUser');
  });
});
