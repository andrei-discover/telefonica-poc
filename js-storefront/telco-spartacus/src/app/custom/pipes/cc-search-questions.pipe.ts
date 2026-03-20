import { Pipe, PipeTransform } from '@angular/core';
import { CcProductQuestions } from '../model/product.model';

@Pipe ({
  name : 'searchQuestions',
  standalone: false 
})
export class SearchQuestionsPipe implements PipeTransform {
  transform(
    value: CcProductQuestions[] | null | undefined,
    search: string | null | undefined
  ): CcProductQuestions[] {
    
    if (!value) return [];

    const term = (search ?? '').toLowerCase().trim();

    if (!term) return value;

    return value.filter((data: CcProductQuestions) =>
      (data.question ?? '').toLowerCase().includes(term) ||
      (data.answer ?? '').toLowerCase().includes(term)
    );
  }
}