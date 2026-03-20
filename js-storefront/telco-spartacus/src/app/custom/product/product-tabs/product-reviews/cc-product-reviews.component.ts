import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  OnDestroy,
} from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { AuthService, ProductReviewService, User } from '@spartacus/core';
import { CurrentProductService, ProductReviewsComponent } from '@spartacus/storefront';
import { filter, Observable, Subject, takeUntil, tap } from 'rxjs';
import { UserAccountFacade } from '@spartacus/user/account/root';
import { Store } from '@ngrx/store';

@Component({
  selector: 'cx-product-reviews',
  templateUrl: './cc-product-reviews.component.html',
  styleUrls: ['./cc-product-reviews.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: false,
})
export class CcProductReviewsComponent extends ProductReviewsComponent implements OnDestroy {
  unsubscribe$: Subject<any> = new Subject();
  userLoggedIn$: Observable<boolean> = this.authService.isUserLoggedIn();
  showReviewName: boolean = true;

  constructor(
    reviewService: ProductReviewService, 
    currentProductService: CurrentProductService, 
    fb: UntypedFormBuilder, 
    cd: ChangeDetectorRef,
    private userAccountFacade: UserAccountFacade,
    private authService: AuthService
  ) {
    super(reviewService, currentProductService, fb, cd);
  }

  ngOnDestroy(): void {
    this.unsubscribe$.complete();
  }

  listUserData() {
    const setReviewerName = (user: User) => {
      this.reviewForm.controls['reviewerName'].setValue(user?.name + user?.lastName!);
      this.showReviewName = false;
    }

    this.userAccountFacade.get().pipe(
      filter(user => !!user),
      takeUntil(this.unsubscribe$),
      tap(setReviewerName)
    ).subscribe()
  }

  
  override initiateWriteReview(): void {
    this.isWritingReview = true;

    this.cd.detectChanges();

    if (this.titleInput && this.titleInput.nativeElement) {
      this.titleInput.nativeElement.focus();
    }

    this.listUserData();

  }
}
