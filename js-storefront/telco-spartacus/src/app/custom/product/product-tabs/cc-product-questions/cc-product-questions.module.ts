import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import {
  CmsConfig,
  I18nModule,
  provideDefaultConfig,
} from '@spartacus/core';
import { CcProductQuestionsComponent } from '.';
import { FormErrorsModule, IconModule, SpinnerModule } from '@spartacus/storefront';
import { SearchQuestionsPipe } from '../../../pipes';

@NgModule({
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FormsModule,
    I18nModule,
    IconModule,
    FormErrorsModule,
    SpinnerModule
  ],
  providers: [
    provideDefaultConfig(<CmsConfig>{
      cmsComponents: {
        CcProductQuestionsTabComponent: {
          component: CcProductQuestionsComponent,
        },
      },
    }),
  ],
  declarations: [
    CcProductQuestionsComponent,
    SearchQuestionsPipe
  ],
  exports: [CcProductQuestionsComponent],
})
export class CcProductQuestionsModule {}
