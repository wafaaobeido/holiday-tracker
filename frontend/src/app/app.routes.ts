import { Routes } from '@angular/router';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { AuthGuard } from './gaurds/auth.guard';

export const routes: Routes = [
    { path: '', loadComponent: () => import('./components/dashboard/dashboard.component').then(m => m.DashboardComponent), canActivate: [AuthGuard]},
    { path: 'dashboard', loadComponent: () => import('./components/dashboard/dashboard.component').then(m => m.DashboardComponent), canActivate: [AuthGuard]},
    { path: 'login', loadComponent: () => import('./components/login/login.component').then(m => m.LoginComponent)},
    { path: 'register', loadComponent: () => import('./components/register/register.component').then(m => m.RegisterComponent) },
    { path: 'user-logs', loadComponent: () => import('./components/user-logs/user-logs.component').then(m => m.UserLogsComponent), canActivate: [AuthGuard] },
    { path: 'holidays', loadComponent: () => import('./components/holiday-list/holiday-list.component').then(m => m.HolidaysComponent), canActivate: [AuthGuard] },
    { path: '**', redirectTo: '' } // Redirect unknown routes to Dashboard

];
