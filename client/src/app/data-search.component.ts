import { Component, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { NgForm } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { Product, User } from './module';
import { DataService } from './service';
import { UserLoginComponent } from './user-login.component';

export interface ProductData {
  selectedProduct: Product;
}

@Component({
  selector: 'app-data-search',
  templateUrl: './data-search.component.html',
  styleUrls: ['./data-search.component.css']
})
export class DataSearchComponent implements OnInit, OnDestroy {

  userQuery = ''
  resultProducts: Product[] = []
  checkPromise:boolean = true
  sub$!: Subscription
  loginUser!: User

  constructor(public dialog: MatDialog, private router: Router, private dataSvc: DataService, private activatedRoute: ActivatedRoute) { }

  @Input()
  search(form: NgForm) {
    this.sub$.unsubscribe()
    const params = {
      query: form.value.query
    }
    this.userQuery = form.value.query
    this.router.navigate(['/search'], { queryParams: params })
  }

  ngOnInit(): void {
    this.userQuery = this.activatedRoute.snapshot.queryParams['query'];

    this.sub$ = this.activatedRoute.queryParams.subscribe(v => {
      console.info(">>>> query: ", v);
      this.checkPromise = true // Activate progress bar animation
      this.dataSvc.getProducts(v['query'])
        .then(p => {
          this.resultProducts = p
          this.resultProducts = this.resultProducts.sort((a,b) => b.productPercentageDiscount - a.productPercentageDiscount)
          this.checkPromise = false // Deactivate progress bar animation
        })
    })
  }

  verifyUserAddWatchlist(product: Product) {
    this.dialog.open(UserLoginComponent, {
      data: {
        selectedProduct: product
      },
    });
  }

  ngOnDestroy() {
    this.sub$.unsubscribe()
  }
}
