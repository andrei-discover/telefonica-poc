import { TranslationChunksConfig, TranslationResources } from '@spartacus/core';
import { pt } from './pt/index';
import { en } from './en/index';

export const ccCustomTranslations: TranslationResources = {
  pt, en
};

export const ccCustomTranslationChunksConfig: TranslationChunksConfig = {
  ccCustom: [
    'ccProductReview',
    'ccProductQuestions',
    'ccGlobalMessage'
  ],
};
