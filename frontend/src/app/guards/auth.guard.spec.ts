import { TestBed } from '@angular/core/testing';
import { AuthGuard } from './auth.guard';
import { provideRouter } from '@angular/router';

describe('AuthGuard', () => {
  let guard: AuthGuard;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideRouter([]), AuthGuard],
    });
    guard = TestBed.inject(AuthGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });
  
  it('should allow navigation if token is valid', () => {
    const mockPayload = { exp: (Date.now() / 1000) + 1000 };
    const encodedPayload = btoa(JSON.stringify(mockPayload));
    sessionStorage.setItem('token', `header.${encodedPayload}.signature`); 
    expect(guard.canActivate()).toBeTrue();
  });

  it('should prevent navigation if token is expired', () => {
    const mockPayload = { exp: (Date.now() / 1000) - 1000 };
    const encodedPayload = btoa(JSON.stringify(mockPayload));
    sessionStorage.setItem('token', `header.${encodedPayload}.signature`);
    expect(guard.canActivate()).toBeFalse();
  });
});
