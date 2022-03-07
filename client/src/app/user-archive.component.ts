import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { Product, User } from './module';
import { DataService } from './service';

@Component({
  selector: 'app-user-archive',
  templateUrl: './user-archive.component.html',
  styleUrls: ['./user-archive.component.css']
})
export class UserArchiveComponent implements OnInit {

  resultProducts: Product[] = []
  checkPromise:boolean = true
  sub$!: Subscription
  username = ''

  constructor(private router: Router, private dataSvc: DataService, private activatedRoute: ActivatedRoute) { }

  ngOnInit(): void {
    this.username = this.activatedRoute.snapshot.params['username']

    this.dataSvc.getFavouriteProduct(this.username)
      .then(p => {
        this.resultProducts = p
        console.log(this.resultProducts[0].log_time)
        this.checkPromise = false // Deactivate progress bar animation
      })
  }

}
