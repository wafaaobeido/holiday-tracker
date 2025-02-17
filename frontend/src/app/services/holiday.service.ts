import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment.docker';
import { Holiday } from '../models/holiday';

@Injectable({
  providedIn: 'root'
})
export class HolidayService {
  private http = inject(HttpClient);
  private apiUrl = environment.holidayUrl;

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

  getAllHolidays(countryCode: string): Observable<any> {
    return this.http.get<Holiday[]>(`${this.apiUrl}all/${countryCode}`);
  }

  getLastCelebratedHolidays(countryCode: string, number: number): Observable<any> {
    return this.http.get<Holiday[]>(`${this.apiUrl}last/${countryCode}/${number}`, { headers : this.getHeaders() });
  }

  getWeekdayHolidays(year: number, countryCodes: string, numberOfHolidays: number ): Observable<any> {
    return this.http.get<Holiday[]>(`${this.apiUrl}weekdaysHoliday?year=${year}&countryCode=${countryCodes}&numberOfHolidays=${numberOfHolidays}`);
  }

  getWeekdayHolidaysCount(year: number, countryCodes: string): Observable<any> {
    return this.http.get<Holiday[]>(`${this.apiUrl}weekdaysHolidayCount?year=${year}&countryCode=${countryCodes}`);
  }

  getCommonHolidays(year: number, country1: string, country2: string): Observable<any> {
    return this.http.get<Holiday[]>(`${this.apiUrl}common?year=${year}&country1=${country1}&country2=${country2}`);
  }
}

