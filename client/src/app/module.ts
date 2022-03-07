export interface Product {
  productName: string
  productCurrentPrice: number
  productPreviousPrice: number
  productDiscountCondition: string
  productPercentageDiscount: number
  productImageUrl: string
  productStoreUrl: string
  supermarketStore: string
}

export interface User {
  username: string
  passcode: string
}

export interface UserProfile extends User {
  email: string
  isVerified: boolean
}
