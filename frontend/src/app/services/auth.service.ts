import { Inject, inject, Injectable } from '@angular/core';
import { environment } from '../../environments/environment.docker';

import {  catchError, Observable, of, shareReplay} from 'rxjs';
import { User } from '../models/User';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { jwtDecode } from "jwt-decode";
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private http = inject(HttpClient);
  private authUrl = environment.authUrl;
  private userCache$: Observable<any> | undefined;


  constructor(private router: Router) {}


  fetchApiKey(): Observable<{ apikey: string }> {
    console.log("📌 Fetching API Key from server...");
    return this.http.get<{ apikey: string }>(`${environment.apiKeyEndpoint}`) .pipe(
      catchError(err => {
        console.error("🔴 Error fetching API key:", err);
        return of({ apikey: '' }); // Return an empty API key to prevent crashes
      })
    );
  }

  private getHeaders(): HttpHeaders {
    let headers = new HttpHeaders();
  
    const apikey = sessionStorage.getItem("apikey");
    if (!apikey) {
      console.log("🔴 API key is missing!");
    } else {
      headers = headers.set("apikey", apikey);
    }
  
    const token = sessionStorage.getItem("token");
    if (token) {
      headers = headers.set("Authorization", `Bearer ${token}`);
    }
  
    return headers;
  }

  login(credentials: { username: string; password: string }): Observable<any> {
        return this.http.post(`${this.authUrl}login`, credentials);
  }

  register(user: User): Observable<any> {
    return this.http.post(`${this.authUrl}register`, user, { headers: this.getHeaders() });
  }

  getUserInfo(token: string): Observable<any> {
    if (!this.userCache$) {
      this.userCache$ = this.http.get(`${this.authUrl}user`, { headers: this.getHeaders() }).pipe(shareReplay(1));
    }
    return this.userCache$;
  }

  logout(): void {
    sessionStorage.removeItem('token');
    this.router.navigate(['/login']);;
  }

  private tokenCheckInterval: any; // Store interval ID

  isTokenExpired(): boolean {
    const token = sessionStorage.getItem('token');
    if (!token || token.split('.').length !== 3) {
      console.error("🔴 Invalid token detected. Forcing expiration.");
      return true; // Treat missing/invalid tokens as expired
    }

    try {
      const decoded: any = jwtDecode(token);
      const expiryTime = decoded.exp * 1000; 
      const currentTime = Date.now(); 

    
      // console.log("📌 Token Expiry Time:", new Date(expiryTime).toLocaleString());
      // console.log("⏳ Current Time:", new Date(currentTime).toLocaleString());
      // console.log("⏳ Time Left:", ((expiryTime - currentTime) / 1000 / 60).toFixed(2), "minutes");

      return currentTime >= expiryTime;

    } catch (error) {
        console.error("🔴 Invalid token parsing error:", error);
      return true; 
    }
  }

  startTokenCheck() {
    if (this.tokenCheckInterval) clearInterval(this.tokenCheckInterval);

    this.tokenCheckInterval = setInterval(() => {

      if (this.isTokenExpired()) {
        console.warn("🔴 Token expired! Redirecting to login...");
        sessionStorage.clear();
        alert("Your session has expired> Please log in! ")
        clearInterval(this.tokenCheckInterval); 
        this.router.navigate(['/login']);
      }
    }, 2000); 
  }

}

