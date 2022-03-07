import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { NavigationExtras } from '@angular/router';
import { ProductData } from './data-search.component';
import { User } from './module';
import { DataService } from './service';

@Component({
  selector: 'app-user-login',
  templateUrl: './user-login.component.html',
  styleUrls: ['./user-login.component.css']
})
export class UserLoginComponent implements OnInit {

  hide = true
  loginUser!: User
  loginForm!: FormGroup
  usernameFC = this.fb.control('', [ Validators.required, Validators.minLength(1) ])
  passwordFC = this.fb.control('', [ Validators.required, Validators.minLength(1) ])

  constructor(@Inject(MAT_DIALOG_DATA) public data: ProductData, private fb: FormBuilder, private router: Router, private dataSvc: DataService) { }

  ngOnInit(): void {
    this.loginForm = this.fb.group({
      username: this.usernameFC,
      passcode: this.passwordFC
    })
  }

  processLogin() {
    const user = this.loginForm.value as User
    const product = this.data.selectedProduct
    this.dataSvc.getLoginCredential(user)
      .then(() => {
        console.info(product.productCurrentPrice)
        this.dataSvc.createNewFavouriteProduct(user, product)
          .then(() => {
            const navigationPacket: NavigationExtras = {
              state: {
                loginuser: user.username
              }
            }
            this.router.navigate(['/'], navigationPacket)
            this.closeDialogBox()
          })
      })
      .catch(p => {
        this.clearInvalidPassword()
      })
  }

  clearInvalidPassword() {
    this.passwordFC.setValue('')
  }

  closeDialogBox() {
    // Empty funtion to call [mat-dialog-close] to close dialog box
  }

}
