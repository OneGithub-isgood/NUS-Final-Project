import { Component, OnInit } from '@angular/core';
import { FormBuilder, NgForm } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { UserLoginComponent } from './user-login.component';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {

  title = 'IBF2021Day24Task';

  constructor(public dialog: MatDialog, private router: Router, private fb: FormBuilder) { }

  ngOnInit(): void {

  }

  search(form: NgForm) {
    const params = {
      query: form.value.query
    }
    form.control.patchValue({query: ''})
    this.router.navigate(['/search'], { queryParams: params })
  }

  verifyLoginCredential() {
    this.dialog.open(UserLoginComponent, { });
  }
}

