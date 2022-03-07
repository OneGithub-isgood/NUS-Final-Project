import { Time } from "@angular/common"

export interface Product {
  productName: string
  productCurrentPrice: number
  productPreviousPrice: number
  productDiscountCondition: string
  productPercentageDiscount: number
  productImageUrl: string
  productStoreUrl: string
  supermarketStore: string
  log_time: Date
}

export interface User {
  username: string
  passcode: string
}

export interface UserProfile extends User {
  email: string
  isVerified: boolean
}
