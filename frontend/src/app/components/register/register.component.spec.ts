import { fakeAsync, TestBed, tick } from '@angular/core/testing';
import { RegisterComponent } from './register.component';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { of } from 'rxjs';

describe('RegisterComponent', () => {
  let component: RegisterComponent;
  let authServiceSpy = jasmine.createSpyObj('AuthService', ['register']);
  let routerSpy = jasmine.createSpyObj('Router', ['navigate']);

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        { provide: AuthService, useValue: authServiceSpy },
        { provide: Router, useValue: routerSpy }
      ]
    });

    component = TestBed.createComponent(RegisterComponent).componentInstance;

    spyOn(sessionStorage, 'getItem').and.returnValue('mockToken'); // ✅ Mock sessionStorage
  });

  it('should call authService.register and navigate on success', fakeAsync(() => {
    authServiceSpy.register.and.returnValue(of({})); // ✅ Mock successful registration
  
    component.register();
  
    expect(authServiceSpy.register).toHaveBeenCalled(); 
  
    tick(100); // ✅ Simulate the `setTimeout()` delay
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/login']); // ✅ Now it should pass
  }));
});
