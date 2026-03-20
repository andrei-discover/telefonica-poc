import { TranslationChunksConfig, TranslationResources } from '@spartacus/core';
import { pt } from './pt/index';
import { en } from './en/index';

export const ccProductTranslations: TranslationResources = {
  pt, en
};

export const ccProductTranslationChunksConfig: TranslationChunksConfig = {
  product: [
    'TabPanelContainer'
  ]
};
