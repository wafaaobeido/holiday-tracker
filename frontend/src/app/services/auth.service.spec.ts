import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { AuthService } from './auth.service';
import { environment } from '../../environments/environment.docker';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AuthService]
    });

    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  it('should fetch API key from backend', () => {
    service.fetchApiKey().subscribe(response => {
      expect(response.apikey).toBe('mock-api-key');
    });

    const req = httpMock.expectOne(environment.apiKeyEndpoint);
    expect(req.request.method).toBe('GET');
    req.flush({ apiKey: 'mock-api-key' });
  });

  it('should log in and return access token', () => {
    const credentials = { username: 'testuser', password: 'password' };
    service.login(credentials).subscribe(response => {
      expect(response.access_token).toBe('mock-token');
    });

    const req = httpMock.expectOne(`${environment.authUrl}login`);
    expect(req.request.method).toBe('POST');
    req.flush({ access_token: 'mock-token' });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
