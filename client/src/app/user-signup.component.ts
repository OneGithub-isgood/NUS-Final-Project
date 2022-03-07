import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { User, UserProfile } from './module';
import { DataService } from './service';

@Component({
  selector: 'app-user-signup',
  templateUrl: './user-signup.component.html',
  styleUrls: ['./user-signup.component.css']
})
export class UserSignupComponent implements OnInit {

  hide = true
  loginUser!: User
  loginForm!: FormGroup
  checkPromise:boolean = false
  usernameFC = this.fb.control('', [ Validators.required, Validators.minLength(6) ])
  passwordFC = this.fb.control('', [ Validators.required, Validators.minLength(8) ])
  emailAddFC = this.fb.control('', [ Validators.required, Validators.minLength(1), Validators.email ])

  constructor(private fb: FormBuilder, private router: Router, private dataSvc: DataService) { }

  ngOnInit(): void {
    this.loginForm = this.fb.group({
      username: this.usernameFC,
      passcode: this.passwordFC,
      email: this.emailAddFC
    })
  }

  processSignup() {
    this.checkPromise = true
    const user = this.loginForm.value as UserProfile
    this.dataSvc.createNewUser(user)
      .then(() => {
        this.checkPromise = false
        this.router.navigate(['/signup/completed'])
        })
      .catch(p => {
        //this.clearInvalidPassword() // Try to clear password after knowing is wrong attempt
      })
  }

}
