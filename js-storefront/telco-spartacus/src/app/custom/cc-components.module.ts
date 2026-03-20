import { NgModule } from '@angular/core';
import { CcProductReviewsModule, CcProductQuestionsModule } from './product';
import { CcTranslationModule } from '../../../public/translations';
import { CcEndpointsModule } from './config/cc-endpoints';
import { CcProductReviewService } from './service';
import { CcProductReviewConnector } from './connector';
import { CcProductReviewAdapter } from './adapter';

@NgModule({
  imports: [
    CcEndpointsModule,
    CcTranslationModule,
    CcProductReviewsModule,
    CcProductQuestionsModule
  ],
  providers: [
    CcProductReviewService,
    CcProductReviewConnector,
    CcProductReviewAdapter,
  ],
  declarations: [],
  exports: [
  ],
})
export class CcComponentsModule {}
