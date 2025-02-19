import { TestBed } from '@angular/core/testing';
import { LoginComponent } from './login.component';
import { provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { of } from 'rxjs';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let authService: AuthService;
  let router: Router;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [
        provideRouter([]),
        provideHttpClient(),
        AuthService,
        LoginComponent
      ],
    }).compileComponents();

    component = TestBed.inject(LoginComponent);
    authService = TestBed.inject(AuthService);
    router = TestBed.inject(Router);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call authService.login on login and navigate on success', () => {
    spyOn(authService, 'login').and.returnValue(of({ access_token: 'mockToken' }));
    spyOn(router, 'navigate');

    component.login();

    expect(authService.login).toHaveBeenCalledWith(component.credentials);
    expect(router.navigate).toHaveBeenCalledWith(['/dashboard']);
  });
});
