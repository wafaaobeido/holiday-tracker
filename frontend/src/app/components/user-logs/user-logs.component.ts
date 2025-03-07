import { ChangeDetectionStrategy, Component, inject, ViewChild } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { UserLogService } from '../../services/user-log.service';
import { Router, RouterModule } from '@angular/router';

import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatIconModule } from '@angular/material/icon';
import { DatePipe } from '@angular/common';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
@Component({
  selector: 'app-user-logs',
  standalone: true,
  imports: [ 
    FormsModule, 
    MatCardModule, 
    MatFormFieldModule,
    MatButtonModule,
    MatInputModule,
    MatTableModule,
    MatToolbarModule,
    MatIconModule,
    DatePipe,
    MatPaginator,
    RouterModule
 ],
  templateUrl: './user-logs.component.html',
  styleUrl: './user-logs.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush 
})
export class UserLogsComponent {
  private userLogService = inject(UserLogService);
  displayedColumns: string[] = ['userName', 'content', 'timestamp'];
  dataSource = new MatTableDataSource<any>([]);

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(private router: Router) {
  }

  ngOnInit() {
    this.loadUserActivities();
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  loadUserActivities(): void {
    let token = sessionStorage.getItem('token') || '';
    this.userLogService.getUserActivities(JSON.parse(atob(token.split('.')[1])).preferred_username).subscribe(
      (data) => {
        this.dataSource.data = [...data];
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
      },
      (error) => console.error('Error loading user activities:', error)
    );
  }

  refreshActivities(): void {
    console.log("Refreshing data...");
    this.loadUserActivities();
  }


  applyFilter(event: Event): void {
    const filterValue = (event.target as HTMLInputElement).value.trim().toLowerCase();
    this.dataSource.filter = filterValue;
  }

  
  goHome() {
    this.router.navigate(['/dashboard']); 
  }

}

