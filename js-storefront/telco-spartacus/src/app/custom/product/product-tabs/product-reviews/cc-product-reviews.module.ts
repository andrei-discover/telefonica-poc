import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import {
  CmsConfig,
  FeaturesConfigModule,
  I18nModule,
  provideDefaultConfig,
} from '@spartacus/core';
import { CcProductReviewsComponent } from '.';
import { FormErrorsModule, FormRequiredAsterisksComponent, FormRequiredLegendComponent, IconModule, ReadMoreComponent, StarRatingModule } from '@spartacus/storefront';

@NgModule({
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FormsModule,
    I18nModule,
    StarRatingModule,
    FormErrorsModule,
    FeaturesConfigModule,
    FormRequiredAsterisksComponent,
    FormRequiredLegendComponent,
    ReadMoreComponent,
    IconModule
  ],
  providers: [
    provideDefaultConfig(<CmsConfig>{
      cmsComponents: {
        ProductReviewsTabComponent: {
          component: CcProductReviewsComponent,
        },
      },
    }),
  ],
  declarations: [CcProductReviewsComponent],
  exports: [CcProductReviewsComponent],
})
export class CcProductReviewsModule {}
