import { NgModule } from '@angular/core';
import { CcProductReviewsModule } from './product';
import { CcTranslationModule } from '../../../public/translations';

@NgModule({
  imports: [
    CcTranslationModule,
    CcProductReviewsModule,
  ],
  providers: [

  ],
  declarations: [],
  exports: [
  ],
})
export class CcComponentsModule {}
