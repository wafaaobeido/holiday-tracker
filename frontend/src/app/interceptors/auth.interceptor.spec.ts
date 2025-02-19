import { fakeAsync, TestBed, tick } from '@angular/core/testing';
import { HTTP_INTERCEPTORS, HttpClient, provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { AuthInterceptor } from './auth.interceptor';
import { AuthService } from '../services/auth.service';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { of, throwError } from 'rxjs';

describe('AuthInterceptor', () => {
  let httpMock: HttpTestingController;
  let httpClient: HttpClient;
  let authServiceMock: jasmine.SpyObj<AuthService>;

  beforeEach(() => {
    authServiceMock = jasmine.createSpyObj<AuthService>('AuthService', ['fetchApiKey']);

    TestBed.configureTestingModule({
      providers: [
        { provide: AuthService, useValue: authServiceMock },
        { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true },
        provideHttpClient(withInterceptorsFromDi()),
        provideHttpClientTesting()
      ]
    });

    httpMock = TestBed.inject(HttpTestingController);
    httpClient = TestBed.inject(HttpClient);
    authServiceMock = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;

    sessionStorage.setItem('token', 'stored-token'); 
  });

  afterEach(() => {
    httpMock.verify(); 
    sessionStorage.clear();
  });

  beforeEach(() => {
    authServiceMock.fetchApiKey.and.returnValue(of({ apikey: 'mock-api-key' })); 
  });

  it('should attach Authorization token from sessionStorage', (done) => {
    sessionStorage.clear(); // ✅ Start fresh
    sessionStorage.setItem('token', 'stored-token'); // ✅ Ensure token is set before request

    console.log("🚀 Token before request:", sessionStorage.getItem("token"));
    expect(sessionStorage.getItem('token')).toBe('stored-token'); // ✅ Double-check it's stored


    httpClient.get('/test').subscribe(response => {
      expect(response).toBeTruthy();
      done();
    });  

    const req = httpMock.expectOne('/test');

    console.log("📌 Headers Before Expectation:", req.request.headers.get('Authorization'));

    const authHeader = req.request.headers.get('Authorization');

    expect(authHeader).toBe('Bearer stored-token');
  
    req.flush({});
  });


  it('should fetch API key if missing and attach it to headers', fakeAsync(() => {
    // Ensure API key is missing and token is set
    sessionStorage.removeItem('apikey');
    sessionStorage.setItem('token', 'stored-token');

    // Simulate fetchApiKey returning a new key
    authServiceMock.fetchApiKey.and.returnValue(of({ apikey: 'new-api-key' }));

    let responseData: any;
    httpClient.get('/test').subscribe(response => {
      responseData = response;
    });

    tick();

    const req = httpMock.expectOne('/test');
    expect(authServiceMock.fetchApiKey).toHaveBeenCalled();
    expect(req.request.headers.get('apikey')).toBe('new-api-key');
    expect(req.request.headers.get('Authorization')).toBe('Bearer stored-token');

    req.flush({ data: 'ok' });
    tick();

    expect(sessionStorage.getItem('apikey')).toBe('new-api-key');
    expect(responseData.data).toBe('ok');
  }));

  it('should handle API key fetch failure gracefully', () => {
    authServiceMock.fetchApiKey.and.returnValue(throwError(() => new Error('API key fetch failed')));

    httpClient.get('/test').subscribe();

    httpMock.expectNone('/key');

    const req = httpMock.expectOne('/test');
    expect(req.request.headers.get('apikey')).toBeNull();

    req.flush({});
  });

  it('should bypass the interceptor for requests containing /key in the URL', () => {
    httpClient.get('/key').subscribe();

    const req = httpMock.expectOne('/key');
    expect(req.request.headers.has('apikey')).toBeFalse();

    req.flush({});
  });
});