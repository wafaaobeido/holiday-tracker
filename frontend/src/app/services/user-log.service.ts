import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment.docker';

@Injectable({
  providedIn: 'root'
})
export class UserLogService {
  private http = inject(HttpClient);
  private apiUrl = environment.userUrl ;

  getUserActivities(userName: string): Observable<any> {
    return this.http.get(`${this.apiUrl}${userName}`);
  }

  getAllUserActivities(): Observable<any> {
    return this.http.get(`${this.apiUrl}all`);
  }
}
