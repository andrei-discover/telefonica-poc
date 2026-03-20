import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { catchError, switchMap } from 'rxjs/operators';
import { GlobalMessageService, GlobalMessageType, UserIdService } from '@spartacus/core';
import { CcProductReviewConnector } from '../connector';
import { CcProductQuestions } from '../model/product.model';

@Injectable({
  providedIn: 'root',
})
export class CcProductReviewService {

  constructor(
    private ccProductReviewConnector: CcProductReviewConnector,
    private globalMessageService: GlobalMessageService,
    private userIdService: UserIdService
  ) {}

  listProductQuestion(productCode: string): Observable<CcProductQuestions[]> {
    return this.ccProductReviewConnector.listProductQuestion(productCode).pipe(
      catchError((error) => {
        this.showErrorMessage();
        throw error
      })
    );
  }

  createProductQuestion(
    payload: { productCode: string, question: string}
  ) {
    return this.ccProductReviewConnector.createProductQuestion(payload).pipe(
      catchError((error) => {
        this.showErrorMessage();
        throw error
      })
    );
  }

  voteProductReview(
    id: string
  ){
    return this.userIdService.takeUserId().pipe(
      switchMap(userId => {
        return this.ccProductReviewConnector.voteProductReview(userId, id).pipe(
          catchError((error) => {
            this.showErrorMessage();
            throw error
          })
        );
      })
    )
  }

  showErrorMessage(): void {
    this.globalMessageService.add(
      { key: 'ccGlobalMessage.requestError' },
      GlobalMessageType.MSG_TYPE_ERROR
    )
  }
}
