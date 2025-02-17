import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RegisterComponent } from './register.component';
import { AuthService } from '../../services/auth.service';
import { RouterTestingModule } from '@angular/router/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormsModule } from '@angular/forms';
import { of, throwError } from 'rxjs';

describe('RegisterComponent', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;

  beforeEach(async () => {
    authServiceSpy = jasmine.createSpyObj('AuthService', ['register']);

    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientTestingModule, FormsModule],
      declarations: [RegisterComponent],
      providers: [{ provide: AuthService, useValue: authServiceSpy }]
    }).compileComponents();

    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;
  });

  it('should create component', () => {
    expect(component).toBeTruthy();
  });

  it('should register user successfully', () => {
    authServiceSpy.register.and.returnValue(of({ message: 'User registered successfully' }));

    component.user = { firstname: 'John', lastname: 'Doe', username: 'testuser', email: 'test@example.com', password: 'password' };
    component.register();

    expect(window.alert).toHaveBeenCalledWith('User registered successfully!');
  });

  it('should show error on registration failure', () => {
    authServiceSpy.register.and.returnValue(throwError(() => new Error('Registration failed')));

    spyOn(window, 'alert');
    component.register();

    expect(window.alert).toHaveBeenCalledWith('Registration failed. Please check your inputs.');
  });
});
