import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http'

import { AppComponent } from './app.component';

import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule } from '@angular/material/dialog';
import { MatInputModule } from '@angular/material/input';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { FlexLayoutModule } from '@angular/flex-layout';

import { DataSearchComponent } from './data-search.component';
import { DataMainComponent } from './data-main.component';
import { DataService } from './service';
import { RouterModule, ROUTER_CONFIGURATION, Routes } from '@angular/router';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { UserLoginComponent } from './user-login.component';
import { UserSignupComponent } from './user-signup.component';
import { UserWatchlistComponent } from './user-watchlist.component';
import { UserAftersignupComponent } from './user-aftersignup.component';


const appRoutes: Routes = [
  { path: '', component: DataMainComponent },
  { path: 'search', component: DataSearchComponent },
  { path: 'login', component: UserLoginComponent },
  { path: 'signup', component: UserSignupComponent },
  { path: 'signup/completed', component: UserAftersignupComponent },
  //{ path: '**', redirectTo: '/', pathMatch: 'full' }
]

@NgModule({
  declarations: [
    AppComponent,
    DataSearchComponent,
    DataMainComponent,
    UserLoginComponent,
    UserSignupComponent,
    UserWatchlistComponent,
    UserAftersignupComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    FormsModule, ReactiveFormsModule,
    BrowserAnimationsModule,
    MatProgressBarModule, MatDialogModule,
    MatIconModule, MatCardModule, MatToolbarModule,
    MatButtonModule, MatInputModule, MatFormFieldModule,
    FlexLayoutModule,
    RouterModule.forRoot(
      appRoutes,
      { useHash: true }
    )
  ],
  providers: [DataService],
  bootstrap: [AppComponent]
})
export class AppModule { }
