import {
  ChangeDetectionStrategy,
  Component,
  OnInit,
} from '@angular/core';
import { FormBuilder, FormControl, UntypedFormGroup, Validators } from '@angular/forms';
import { BehaviorSubject, debounceTime, map, Observable, Subject, take, takeUntil, tap} from 'rxjs';
import { CcProductReviewService } from '../../../service';
import { AuthService, GlobalMessageService, GlobalMessageType, Product, RoutingService } from '@spartacus/core';
import { CurrentProductService } from '@spartacus/storefront';
import { CcProductQuestions } from '../../../model/product.model';

@Component({
  selector: 'cx-product-questions',
  templateUrl: './cc-product-questions.component.html',
  styleUrls: ['./cc-product-questions.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: false,
})
export class CcProductQuestionsComponent implements OnInit {
  searchboxControl = new FormControl('');
  form: UntypedFormGroup = this.fb.group({
    question: ['', Validators.required],
  });
  productCode: string = "";
  unsubscribe$: Subject<any> = new Subject();
  product$: Observable<Product | null> =
    this.currentProductService.getProduct().pipe(
      tap(product => { 
        this.productCode = product?.code || '';
        this.listProductQuestion(product?.code!) 
      })
    );
  isCreatePage: boolean = false;
  productQuestions$ = new BehaviorSubject<CcProductQuestions[] | null>([])
  userLoggedIn$: Observable<boolean> = this.authService.isUserLoggedIn()

  constructor(
    private fb: FormBuilder,
    private ccProductReviewService: CcProductReviewService,
    private currentProductService: CurrentProductService,
    private globalMessageService: GlobalMessageService,
    private authService: AuthService,
    private routingService: RoutingService
  ) {}

  ngOnInit(): void {
    this.getChangesSeachboxControl();
  }

  ngOnDestroy(): void {
    this.unsubscribe$.complete();
  }

  getChangesSeachboxControl() {
    this.searchboxControl.valueChanges.pipe(
      debounceTime(1000),
      map(value => value ?? ''),
      tap(terms =>  {
        if(terms?.length === 0) this.reset();
      })
    ).subscribe()
  }

  reset() {
    this.searchboxControl.setValue("");
  }

  createQuestion(productCode: string) {
    const questionForm = this.form.controls['question'].value;

    if(this.form.valid) {
      this.ccProductReviewService.createProductQuestion({productCode: productCode, question: questionForm }).pipe(
        takeUntil(this.unsubscribe$)
      ).subscribe({
        complete:() => {
          this.showSuccessMessage();
          this.form.controls['question'].reset();
          this.isCreatePage = false;
          this.listProductQuestion(this.productCode);
        }
      })
    }
  }

  listProductQuestion(productCode: string) {
    const setReviewList = (response: CcProductQuestions[]) => {
      this.productQuestions$.next(response)
    }

    this.ccProductReviewService.listProductQuestion(productCode).pipe(
      takeUntil(this.unsubscribe$),
      tap(setReviewList)
    ).subscribe()
  }

  showSuccessMessage(): void {
    this.globalMessageService.add(
      { key: 'ccGlobalMessage.postQuestionSuccess' },
      GlobalMessageType.MSG_TYPE_CONFIRMATION
    )
  }

  showCreateTemplate() {
    const validateLogin = (isLogged: boolean) => {
      if (isLogged) {
        this.isCreatePage = true;
      } else {
        this.routingService.go({ cxRoute: 'login' });
      }
    }

    this.userLoggedIn$.pipe(
      take(1),
      takeUntil(this.unsubscribe$),
      tap(validateLogin)
    ).subscribe();
  }

}
