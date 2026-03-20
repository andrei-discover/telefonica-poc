import { NgModule } from '@angular/core';
import { I18nConfig, provideConfig } from "@spartacus/core";
import { ccCustomTranslations, ccCustomTranslationChunksConfig } from './custom/ccCustomTranslations';

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
  ],
})
export class CcTranslationModule { }
