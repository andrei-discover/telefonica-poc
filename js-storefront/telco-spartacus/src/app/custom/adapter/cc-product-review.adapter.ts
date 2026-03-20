import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { OccEndpointsService } from '@spartacus/core';
import { CcProductQuestions } from '../model/product.model';

@Injectable({ providedIn: 'root' })
export class CcProductReviewAdapter {
  constructor(
    protected http: HttpClient,
    protected occEndpointsService: OccEndpointsService
  ) {}

  createProductQuestion(
    payload: { productCode: string, question: string}
  ) {
    const url = this.occEndpointsService.buildUrl('ccProductQuestions');
    return this.http.post<{}>(url, payload);
  }

  listProductQuestion(
    productCode: string
  ) {
    const url = this.occEndpointsService.buildUrl('ccProductQuestions', {
      queryParams: { productCode: productCode },
    });
    return this.http.get<CcProductQuestions[]>(url, {});
  }

}
