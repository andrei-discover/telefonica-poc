import { Pipe, PipeTransform } from '@angular/core';
import { CcProductQuestions } from '../model/product.model';

@Pipe ({
  name : 'searchQuestions',
  standalone: false 
})
export class SearchQuestionsPipe implements PipeTransform {
  transform(value: CcProductQuestions[], ...args: any[]): CcProductQuestions[] {
    let searchQuestion = '';

    if(args.length !== 0) {
      searchQuestion = args[0].toLowerCase();
      return value.filter((data:CcProductQuestions)=> data.question?.toLowerCase().includes(searchQuestion));
    } 

    return value
  }
}