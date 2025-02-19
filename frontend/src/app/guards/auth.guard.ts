import { inject, Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { jwtDecode } from 'jwt-decode';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {
  private router = inject(Router);

  canActivate(): boolean {
    const token = sessionStorage.getItem('token');

    if (!token || this.isTokenExpired(token)) {
      sessionStorage.clear();
      this.router.navigate(['/login']);
      return false; 
    }
    
    return true; 
  }

  isTokenExpired(token: string): boolean {
    const decoded: any = jwtDecode(token);
    return decoded.exp * 1000 < Date.now();
  }
}

