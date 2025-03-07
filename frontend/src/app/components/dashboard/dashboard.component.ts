import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { CommonModule } from '@angular/common';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatCardModule } from '@angular/material/card';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule, MatToolbarModule, MatCardModule],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class DashboardComponent {
  username: string = '';

  constructor(private authService: AuthService, private router: Router) {}

  ngOnInit() {
    const token = sessionStorage.getItem('token');
    if (!token) {
      this.router.navigate(['/login']);
    } else {
      this.username = JSON.parse(atob(token.split('.')[1])).preferred_username;
      this.authService.startTokenCheck();
    }
  }


  goBack() {
    this.router.navigate(['/dashboard']); 
  }

  isUserLoggedIn(): boolean {
    return !!sessionStorage.getItem('token'); 
  }

  navigateToRegister() {
    this.router.navigate(['/register']);
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
