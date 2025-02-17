import { Component, inject, OnInit } from '@angular/core';
import { Router, RouterOutlet } from '@angular/router';
import { AuthService } from './services/auth.service';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent implements OnInit {
  title = 'frontend';

  constructor(private authService: AuthService, private router: Router) {}

  ngOnInit(): void {
    if (sessionStorage.getItem("token")){
    console.log("🚀 Starting token expiration check...");
    this.authService.startTokenCheck();
    }
  }

  toggleDarkMode(): void {
    document.body.classList.toggle('dark-mode');
  }
 

}
