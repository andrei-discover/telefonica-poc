import { NgModule } from '@angular/core';
import { provideConfig } from '@spartacus/core';
import { ccEndpointsConfig } from './cc-endpoint.config';

@NgModule({
  providers: [provideConfig(ccEndpointsConfig)],
})
export class CcEndpointsModule {}
