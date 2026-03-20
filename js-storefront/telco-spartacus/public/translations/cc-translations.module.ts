import { NgModule } from '@angular/core';
import { I18nConfig, provideConfig } from "@spartacus/core";
import { ccCustomTranslations, ccCustomTranslationChunksConfig } from './custom/ccCustomTranslations';
import { ccProductTranslationChunksConfig, ccProductTranslations } from './product/ccProductTranslations';

@NgModule({
  declarations: [],
  imports: [],
  providers: [
    provideConfig(<I18nConfig>{
      i18n: {
        resources: ccCustomTranslations,
        chunks: ccCustomTranslationChunksConfig,
      },
    }),
    provideConfig(<I18nConfig>{
      i18n: {
        resources: ccProductTranslations,
        chunks: ccProductTranslationChunksConfig,
      },
    }),
  ],
})
export class CcTranslationModule { }
