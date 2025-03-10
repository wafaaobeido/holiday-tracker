import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent } from '@angular/common/http';
import { catchError, from, Observable, switchMap } from 'rxjs';
import { AuthService } from '../services/auth.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private authService: AuthService) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    console.log("🔄 Interceptor triggered:", req.url);
    console.log("🔑 API key in sessionStorage:", sessionStorage.getItem('apikey'));
    console.log("📌 Token in sessionStorage:", sessionStorage.getItem('token'));
    
  if (req.url.includes('/key')) {
    return next.handle(req);
  }

  let apiKey = sessionStorage.getItem('apikey');

  // If API key is missing, fetch it before proceeding
  if (!apiKey) {
    console.log("🔑 API key missing, fetching...");

    return from(this.authService.fetchApiKey()).pipe(
      switchMap((response) => {
        if (!response || !response.apikey) {
          throw new Error("Invalid API Key response");
      }
        sessionStorage.setItem('apikey', response.apikey);
        console.log("✅ API Key Set in SessionStorage:", response.apikey);


        return this.handleRequestWithHeaders(req, next);
      }),
      catchError((err) => {
          console.error("🔴 API Key Fetch Error:", err);
          return next.handle(req);
      })
    );
  }

  return this.handleRequestWithHeaders(req, next);
}

private handleRequestWithHeaders(req: HttpRequest<any>, next: HttpHandler) {
  let apiKey = sessionStorage.getItem('apikey') || '';
  let token = sessionStorage.getItem('token') || '';

  let headers = req.headers;
  if (apiKey) headers = headers.set("apikey", apiKey);
  if (token) headers = headers.set("Authorization", `Bearer ${token}`);

  console.log("📌 Modified Headers:", headers.get('Authorization'), headers.get('apikey'));

  const modifiedReq = req.clone({ headers });
  return next.handle(modifiedReq);
}

}