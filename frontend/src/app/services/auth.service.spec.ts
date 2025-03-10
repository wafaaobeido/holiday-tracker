import { fakeAsync, TestBed, tick } from '@angular/core/testing';
import { AuthService } from './auth.service';
import { withInterceptorsFromDi } from '@angular/common/http';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { Router } from '@angular/router';
import { environment } from '../../environments/environment.docker';
import { User } from '../models/User';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;
  let routerMock = { navigate: jasmine.createSpy('navigate') };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        AuthService,
        provideHttpClient(withInterceptorsFromDi()),
        provideHttpClientTesting(),
        { provide: Router, useValue: routerMock }
      ]
    });

    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });


  it('should fetch API key when requested', () => {
    service.fetchApiKey().subscribe(response => {
      expect(response.apikey).toBe('mock-api-key');
    });

    const req = httpMock.expectOne(`${environment.apiKeyEndpoint}`);
    expect(req.request.method).toBe('GET');

    req.flush({ apikey: 'mock-api-key' });
  });

  it('should handle API key fetch error', () => {
    service.fetchApiKey().subscribe(response => {
      expect(response.apikey).toBe('');
    });

    const req = httpMock.expectOne(`${environment.apiKeyEndpoint}`);
    req.error(new ErrorEvent('Network error'));
  });

  it('should return headers with API key and token', () => {
    sessionStorage.setItem('apikey', 'mock-api-key');
    sessionStorage.setItem('token', 'stored-token');

    const headers = service['getHeaders']();

    expect(headers.get('apikey')).toBe('mock-api-key');
    expect(headers.get('Authorization')).toBe('Bearer stored-token');
  });

  it('should handle missing API key in headers', () => {
    sessionStorage.removeItem('apikey');
    const headers = service['getHeaders']();
    expect(headers.get('apikey')).toBeNull();
  });

  it('should call login API and return response', () => {
    const mockResponse = { token: 'jwt-token' };
    const credentials = { username: 'user', password: 'password' };

    service.login(credentials).subscribe(response => {
      expect(response).toEqual(mockResponse);
    });

    const req = httpMock.expectOne(`${environment.authUrl}login`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(credentials);

    req.flush(mockResponse);
  });

  it('should call register API and return response', () => {
    const mockResponse = { message: 'User registered successfully' };
    const mockUser: User = {firstname:'New', lastname: 'Test', username: 'test', email:'test@example.com', password: 'test123' };

    service.register(mockUser).subscribe(response => {
      expect(response).toEqual(mockResponse);
    });

    const req = httpMock.expectOne(`${environment.authUrl}register`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(mockUser);

    req.flush(mockResponse);
  });

  it('should fetch user info and cache the response', () => {
    const mockResponse = { id: 1, username: 'testuser' };

    service.getUserInfo('jwt-token').subscribe(response => {
      expect(response).toEqual(mockResponse);
    });

    const req = httpMock.expectOne(`${environment.authUrl}user`);
    expect(req.request.method).toBe('GET');
    req.flush(mockResponse);

    // Second call should use cached response (no new request)
    service.getUserInfo('jwt-token').subscribe(response => {
      expect(response).toEqual(mockResponse);
    });

    httpMock.expectNone(`${environment.authUrl}user`);
  });

  it('should remove token and navigate to login on logout', fakeAsync(() => {
    sessionStorage.setItem('token', 'jwt-token');
  
    service.logout();
    tick(); // Ensures Angular updates execution queue
  
    expect(sessionStorage.getItem('token')).toBeNull();
    expect(routerMock.navigate).toHaveBeenCalledWith(['/login']);
  }));

  it('should detect expired token', () => {
    spyOn(service, 'isTokenExpired').and.returnValue(true);
    expect(service.isTokenExpired()).toBeTrue();
  });

  it('should handle expired token and clear session storage', (done) => {
    spyOn(service, 'isTokenExpired').and.returnValue(true);
    spyOn(window, 'alert');

    service.startTokenCheck();
    
    setTimeout(() => {
      expect(sessionStorage.getItem('token')).toBeNull();
      expect(routerMock.navigate).toHaveBeenCalledWith(['/login']);
      expect(window.alert).toHaveBeenCalledWith("Your session has expired> Please log in! ");
      done();
    }, 2500); 
  });

});