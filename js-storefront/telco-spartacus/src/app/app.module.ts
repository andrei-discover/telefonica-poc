import { HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { EffectsModule } from '@ngrx/effects';
import { StoreModule } from '@ngrx/store';
import { translationChunksConfig, translations } from '@spartacus/assets';
import { ConfigModule, FeaturesConfig, I18nConfig, provideConfig } from '@spartacus/core';
import { tmaTranslations, TmaSpartacusModule } from '@tua/tua-spa';
import { AppRoutingModule } from "@spartacus/storefront";
import { SpartacusConfigurationModule } from './spartacus/spartacus-configuration.module';
import { AppComponent } from './app.component';
import { CcComponentsModule } from './custom';


@NgModule({
  declarations: [AppComponent],
  imports: [
    AppRoutingModule,
    BrowserModule,
    HttpClientModule,
    StoreModule.forRoot({}),
    EffectsModule.forRoot([]),
    TmaSpartacusModule,
    SpartacusConfigurationModule,
    CcComponentsModule,
    ConfigModule.withConfig({
      i18n: {
        resources: tmaTranslations,
      },
    })
  ],
  providers: [
    provideConfig(<I18nConfig>{
      // we bring in static translations to be up and running soon right away
      i18n: {
        resources: translations,
        chunks: translationChunksConfig,
        fallbackLang: 'en',
      },
    }),
    provideConfig(<FeaturesConfig>{
      features: {
        level: '*',
        directDebitFeature: true,
        purchaseWithAssuranceFeature: true
      },
    }),
  ],
  bootstrap: [AppComponent],
})
export class AppModule {
}