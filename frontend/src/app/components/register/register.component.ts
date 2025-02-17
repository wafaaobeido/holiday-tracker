import { ChangeDetectionStrategy, Component } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import {MatCardModule} from '@angular/material/card';
import {MatFormFieldModule} from '@angular/material/form-field';
import { FormsModule } from '@angular/forms';
import { User } from '../../models/User';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { take } from 'rxjs';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [ 
    FormsModule, 
    MatCardModule,    
     MatInputModule,
    MatFormFieldModule,
    MatButtonModule 
  ],
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush 
})
export class RegisterComponent {

  isSubmitting: boolean = false;
  user: User = {
    firstname: '',
    lastname: '',
    username: '',
    email: '',
    password: ''
  };
  constructor(private authService: AuthService, private router: Router) {}

  register(): void {
    const token = sessionStorage.getItem('token');
    if (!token) {
      alert('You must be logged in to register a new user.');
      return;
    }

    if (this.isSubmitting) return; // Prevents multiple clicks
    this.isSubmitting = true;

    this.authService.register(this.user).pipe(take(1)).subscribe({ 
      next: () => {
        setTimeout(() => {
          alert('User registered successfully!');
          this.router.navigate(['/login']);
        }, ( 100 ));
        }, error: (err) => {
          console.error('Registration failed.', err);
          this.isSubmitting = false; 
        }
    });
  }

  

}
