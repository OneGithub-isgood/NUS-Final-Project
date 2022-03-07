import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { lastValueFrom } from "rxjs";
import { Product, User } from "./module";

@Injectable()
export class DataService {

  resultProducts: Product[] = []
  checkPromise:boolean = true

  constructor(private http: HttpClient) {}

  getProducts(product: string): Promise<Product[]> {
    const params = new HttpParams().set("product", product)
    return lastValueFrom(
      this.http.get<Product[]>("/api/search", { params }) //http:localhost:8080/api/search?product=ben+jerry
    )
  }

  getLoginCredential(user: User): Promise<void> {
    console.log(user)
    return lastValueFrom(this.http.post<void>('/api/login', user)) //http:localhost:8080/api/login
  }

  createNewUser(user: User): Promise<void> {
    return lastValueFrom(this.http.post<void>('/api/signup', user)) //http:localhost:8080/api/signup
  }

  createNewFavouriteProduct(user: User, product: Product): Promise<void> {
    const body = {user: user, product: product}
    console.log(body)
    return lastValueFrom(this.http.post<void>('/api/favproduct', body)) //http:localhost:8080/api/favproduct
  }

  getFavouriteProduct(username: string): Promise<Product[]> {
    return lastValueFrom(
      this.http.get<Product[]>(`/api/archive/${username}`)) //http:localhost:8080/api/archive/username
  }
}
