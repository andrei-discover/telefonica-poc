import { Injectable } from "@angular/core";
import { CcProductReviewAdapter } from "../adapter";

@Injectable({providedIn: 'root'})
export class CcProductReviewConnector {
  constructor(
    protected ccProductReviewAdapter: CcProductReviewAdapter
  ) { }

  createProductQuestion(
    payload: { productCode: string, question: string}
  ) {
    return this.ccProductReviewAdapter.createProductQuestion(payload);
  }

  listProductQuestion(
    productCode: string
  ) {
    return this.ccProductReviewAdapter.listProductQuestion(productCode);
  }

  voteProductReview(
    userId: string,
    id: string
  ){
    return this.ccProductReviewAdapter.voteProductReview(userId, id);
  }


}
