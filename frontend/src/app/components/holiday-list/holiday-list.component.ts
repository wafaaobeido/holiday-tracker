import { ChangeDetectionStrategy, ChangeDetectorRef, Component, inject, ViewChild } from '@angular/core';
import { HolidayService } from '../../services/holiday.service';

import { MatTableDataSource } from '@angular/material/table';
import { MatSort } from '@angular/material/sort';
import {MatCardModule} from '@angular/material/card';
import { MatFormFieldModule, MatLabel} from '@angular/material/form-field';
import {ReactiveFormsModule, FormsModule, AbstractControl, ValidationErrors } from '@angular/forms';
import {MatIconModule} from '@angular/material/icon';
import {MatToolbarModule} from '@angular/material/toolbar';
import {MatTableModule} from '@angular/material/table';
import { CommonModule } from '@angular/common';
import { MatPaginator } from '@angular/material/paginator';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { Router, RouterModule } from '@angular/router';
import { HolidayResponse } from '../../models/holiday-response';
import { Holiday } from '../../models/holiday';
import { MatOption } from '@angular/material/core';
import { MatSelect } from '@angular/material/select';
import { MatSnackBar } from '@angular/material/snack-bar';
import { HolidayCount } from '../../models/holiday-count';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-holidays',
  standalone: true,
  imports: [
    FormsModule,
    CommonModule,
    MatCardModule,
    MatFormFieldModule,
    ReactiveFormsModule,
    MatButtonModule,
    MatInputModule,
    MatIconModule,
    MatToolbarModule,
    MatTableModule,
    MatPaginator,
    RouterModule,
    MatOption,
    MatSelect,
    MatLabel
  ],
  templateUrl: './holiday-list.component.html',
  styleUrls: ['./holiday-list.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class HolidaysComponent {
  holidayForm: FormGroup;

  private holidayService = inject(HolidayService);
  displayedColumns: string[] = ['countryCode', 'name', 'date'];
  dataSource = new MatTableDataSource<any>([]);

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  holidays: Holiday[] = [];
  holidaysCount: HolidayCount[] = [];
  holidaysResponse: HolidayResponse = {data: {}, errors: {}};
  fakeYear: boolean = true;
  count: boolean = false;;
  selectedCountry: string = '';
  holidaysFlat: Holiday[] = [];


  constructor(private router: Router, private cd: ChangeDetectorRef,
    private snackBar: MatSnackBar, private fb: FormBuilder
  ) {
    this.holidayForm = this.fb.group({
      year: ['', [ Validators.min(1900), Validators.max(new Date().getFullYear())]],  
      countryCode: ['', [Validators.required, this.countryCodeValidator.bind(this)]], 
      number: ['', [Validators.pattern('^[1-9][0-9]*$')]],  
      country2: ['', [Validators.pattern('^([A-Z]{2,3})$')]] 
    });
  }

  
  multipleCodePattern = /^([A-Z]{2,3})(,[A-Z]{2,3})*$/;

  countryCodeValidator(control: AbstractControl): ValidationErrors | null {
    if (!control.value) return null;  // Allow empty value (optional)

    const value: string = control.value.trim();

    // **Check if input matches the multiple country code pattern**
    if (!this.multipleCodePattern.test(value)) {
        return { "invalidFormat": "Country codes must be 2-3 uppercase letters, separated by commas" };
    }

    return null; // ✅ Validation passed
  }

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }


  embtyData(){
    this.holidays = [];
    this.holidaysCount = []
    this.holidaysResponse = {data: {}, errors: {}}; 
    this.dataSource.data = []
  }

  get formControls() {
    return this.holidayForm.controls;
  }

  refreshTable(data: Holiday[]){
    this.dataSource.data = [...data];
    this.cd.detectChanges();

    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  } 

  fetchTheRightHolidays() {
    this.embtyData();

    if (this.holidayForm.valid) {
    
    const apiCalls: { [key: string]: () => any } = {
    "countryCode": this.fetchAllHolidays.bind(this),
    "countryCode-number": this.fetchLastCelebratedHolidays.bind(this),
    "countryCode-year": this.fetchWeekdayHolidaysCount.bind(this),
    "countryCode-number-year" : this.fetchWeekdayHolidays.bind(this),
    "countryCode-year-country2": this.fetchCommonHolidays.bind(this)
    };


    const key = [
      this.formControls['countryCode'].value ? "countryCode" : "",
      this.formControls['number'].value ? "number" : 0,
      this.formControls['year'].value ? "year" : 0,
      this.formControls['country2'].value ? "country2" : ""
    ]
      .filter(Boolean)
      .join("-");

      if (apiCalls[key]) {
        if (key === "countryCode-year") this.count == true;
        else this.count == false;
        apiCalls[key]();
      } else if (key){
        this.showAlert('❌ The combination of values is not correct. Please enter another values!');
      } 
      
    }
      else {
        this.dataSource.data = [];
        this.showAlert('❌ Please enter at least a country code!');
      }

  }

  showAlert(message: string, action: string = 'OK') {
    this.snackBar.open(message, action, {
      duration: 200000, // Auto close after 3 seconds
      horizontalPosition: 'center',
      verticalPosition: 'top', 
      panelClass: ['custom-snackbar'] 
    });
  }

  fetchAllHolidays(){
    if(!this.formControls['countryCode'].value) alert("Please enter Country Code");
    this.holidayService.getAllHolidays(this.formControls['countryCode'].value).subscribe(
      (data) => {
        this.holidaysResponse = data;
        this.processHolidaysData();
      },
      (error) => console.error('Error fetching holidays:', error)
    );
  }


  refreshHolidays(): void {
    this.fetchTheRightHolidays();
  }

  fetchLastCelebratedHolidays() {
    if (this.formControls['countryCode'].value.trim() === '' || this.formControls['number'].value === 0) alert('Please enter Country Code and User ID');
    this.holidayService.getLastCelebratedHolidays(this.formControls['countryCode'].value,this.formControls['number'].value )
      .subscribe(data => {
        this.holidaysResponse = data;
        this.processHolidaysData();
      }, error => {
        console.error('Error fetching last holidays:', error);
      });
  }

  fetchWeekdayHolidaysCount() {
    if (this.formControls['year'].value == null || this.formControls['countryCode'].value.trim() === '') alert('Please enter Year and Country Code');
    this.holidayService.getWeekdayHolidaysCount(this.formControls['year'].value, this.formControls['countryCode'].value)
      .subscribe(data => {
        this.count = true;
        this.holidaysCount = data;
        this.refreshTable([]);
      }, error => {
        console.error('Error fetching weekday holidays:', error);
      });
  }

 fetchWeekdayHolidays(): void {
  this.holidayService.getWeekdayHolidays(this.formControls['year'].value, this.formControls['countryCode'].value, this.formControls['number'].value)
    .subscribe(data => {
        this.holidaysResponse = data;
        this.processHolidaysData();
    }, error => {
        console.error('Error fetching weekday holidays:', error);
    });
  }

  processHolidaysData(): void {
    const flat: Holiday[] = [];
    const data = this.holidaysResponse?.data;
    if (!data) { return; }

    Object.keys(data).forEach(countryCode => {
      if (this.selectedCountry && this.selectedCountry !== countryCode) {
        return;
      }
      const holidays = data[countryCode];
      holidays.forEach((holiday: any, index: number) => {
        flat.push({
          countryCode: index === 0 ? countryCode : '',
          name: holiday.name,
          date: holiday.date
        });
      });
    });
    this.holidaysFlat = flat;
    this.refreshTable(flat);
  }

  fetchCommonHolidays() {
    if (this.formControls['year'].value == null || this.formControls['countryCode'].value.trim() === '' || this.formControls['country2'].value.trim() === '')
      alert('Please enter Year and two Country Codes');

    this.holidayService.getCommonHolidays(this.formControls['year'].value, this.formControls['countryCode'].value, this.formControls['country2'].value)
      .subscribe(data => {
        this.holidays = data;
        this.refreshTable(this.holidays);
      }, error => {
        console.error('Error fetching common holidays:', error);
      });
  }

  onCountryChange(): void {
    this.processHolidaysData();
  }

  getCountryKeys(): string[] {
    return this.holidaysResponse?.data ? Object.keys(this.holidaysResponse.data) : [];
  }

  objectKeys(obj: any): string[] {
    return obj ? Object.keys(obj) : [];
  }

  goHome() {
    this.router.navigate(['/dashboard']);
  }
}
