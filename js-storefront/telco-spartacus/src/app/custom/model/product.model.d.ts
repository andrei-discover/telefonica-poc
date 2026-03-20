import { Review } from '@spartacus/core'

declare module '@spartacus/core' {
  interface Review {
    verifiedPurchase?: boolean
    usefulCount?: number
    userMarkedUseful?: boolean
    id?: string
  }
}

export interface CcProductQuestions {
  productCode?: string
  question?: string
  status?: string
  author?: string
  createdDate?: number
  answer?: string
}

