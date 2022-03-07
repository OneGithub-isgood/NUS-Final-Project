import { Component, OnDestroy, OnInit } from '@angular/core';
import { NavigationExtras, Router } from '@angular/router';
import { User } from './module';
import { DataService } from './service';

@Component({
  selector: 'app-data-main',
  templateUrl: './data-main.component.html',
  styleUrls: ['./data-main.component.css']
})
export class DataMainComponent implements OnInit, OnDestroy {

  loginUser = ""

  constructor(private router: Router, private dataSvc: DataService) { }

  ngOnInit(): void {
    const navigationPacket = this.router.getCurrentNavigation()
    const packetValue = navigationPacket?.extras.state as {
      username: string
    }
    this.loginUser = history.state.loginuser
    console.info(this.loginUser)
  }

  ngOnDestroy() {
    const navigationPacket: NavigationExtras = {
      state: {
        loginuser: this.loginUser
      }
    }
  }


}
