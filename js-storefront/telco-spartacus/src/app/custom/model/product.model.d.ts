import { Review } from '@spartacus/core'

declare module '@spartacus/core' {
  interface Review {
    verifiedPurchase?: boolean
    usefulCount?: number
    userMarkedUseful?: boolean
    id?: string
  }
}

