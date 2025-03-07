import { ComponentFixture, TestBed } from '@angular/core/testing';
import { LoginComponent } from './login.component';
import { AuthService } from '../../services/auth.service';
import { RouterTestingModule } from '@angular/router/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormsModule } from '@angular/forms';
import { of, throwError } from 'rxjs';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;

  beforeEach(async () => {
    authServiceSpy = jasmine.createSpyObj('AuthService', ['login']);

    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientTestingModule, FormsModule],
      declarations: [LoginComponent],
      providers: [{ provide: AuthService, useValue: authServiceSpy }]
    }).compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
  });

  it('should create component', () => {
    expect(component).toBeTruthy();
  });

  it('should log in successfully', () => {
    authServiceSpy.login.and.returnValue(of({ access_token: 'mock-token' }));

    component.credentials = { username: 'testuser', password: 'password' };
    component.login();

    expect(localStorage.getItem('token')).toBe('mock-token');
  });

  it('should show error on login failure', () => {
    authServiceSpy.login.and.returnValue(throwError(() => new Error('Login failed')));

    spyOn(window, 'alert');
    component.credentials = { username: 'testuser', password: 'wrong' };
    component.login();

    expect(window.alert).toHaveBeenCalledWith('Login failed! Please check your credentials.');
  });
});
