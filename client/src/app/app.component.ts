import { Component, OnInit } from '@angular/core';
import { FormBuilder, NgForm } from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {

  title = 'IBF2021Day24Task';

  constructor(private router: Router, private fb: FormBuilder) { }

  ngOnInit(): void {

  }

  search(form: NgForm) {
    const params = {
      query: form.value.query
    }
    form.control.patchValue({query: ''})
    this.router.navigate(['/search'], { queryParams: params })
  }

}

