package br.com.telefonica.core.service;

import br.com.telefonica.core.event.VerifiedPurchaseCheckEvent;
import br.com.telefonica.core.service.impl.DefaultTelefonicaCustomerReviewService;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.customerreview.enums.CustomerReviewApprovalType;
import de.hybris.platform.customerreview.model.CustomerReviewModel;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.model.ModelService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class DefaultTelefonicaCustomerReviewServiceTest {

	private static class TestableTelefonicaCustomerReviewService
		extends DefaultTelefonicaCustomerReviewService
	{

		private final ModelService modelServiceOverride;

		TestableTelefonicaCustomerReviewService(final ModelService modelService) {
			this.modelServiceOverride = modelService;
		}

		@Override
		public ModelService getModelService() {
			return modelServiceOverride;
		}
	}

	@Mock
	private EventService eventService;

	@Mock
	private ModelService modelService;

	@Mock
	private CustomerReviewModel review;

	@Mock
	private UserModel user;

	@Mock
	private ProductModel product;

	private TestableTelefonicaCustomerReviewService service;

	private static final Double RATING    = 4.5;
	private static final String HEADLINE  = "Ótimo produto";
	private static final String COMMENT   = "Recomendo!";
	private static final PK     REVIEW_PK = PK.fromLong(123456789L);

	@BeforeEach
	void setUp() {
		service = spy(new TestableTelefonicaCustomerReviewService(modelService));
		service.setEventService(eventService);
		doReturn(review).when(service)
			.createCustomerReview(RATING, HEADLINE, COMMENT, user, product);
		given(review.getPk()).willReturn(REVIEW_PK);
	}

	@Nested
	class CreateCustomerReviewTest {

		@Test
		void shouldReturnReviewFromSuperCall() {
			CustomerReviewModel result =
				service.createCustomerReview(RATING, HEADLINE, COMMENT, user, product);

			assertThat(result).isSameAs(review);
		}

		@Test
		void shouldSetApprovalStatusToPending() {
			service.createCustomerReview(RATING, HEADLINE, COMMENT, user, product);

			then(review).should().setApprovalStatus(CustomerReviewApprovalType.PENDING);
		}

		@Test
		void shouldSetVerifiedPurchaseToFalse() {
			service.createCustomerReview(RATING, HEADLINE, COMMENT, user, product);

			then(review).should().setVerifiedPurchase(false);
		}

		@Test
		void shouldSaveReviewViaModelService() {
			service.createCustomerReview(RATING, HEADLINE, COMMENT, user, product);

			then(modelService).should(times(1)).save(review);
		}

		@Test
		void shouldPublishVerifiedPurchaseCheckEvent() {
			service.createCustomerReview(RATING, HEADLINE, COMMENT, user, product);

			ArgumentCaptor<VerifiedPurchaseCheckEvent> captor =
				ArgumentCaptor.forClass(VerifiedPurchaseCheckEvent.class);
			then(eventService).should().publishEvent(captor.capture());

			assertThat(captor.getValue().getReviewPk()).isEqualTo(REVIEW_PK);
		}

		@Test
		void shouldPublishEventWithCorrectReviewPk() {
			service.createCustomerReview(RATING, HEADLINE, COMMENT, user, product);

			ArgumentCaptor<VerifiedPurchaseCheckEvent> captor =
				ArgumentCaptor.forClass(VerifiedPurchaseCheckEvent.class);
			then(eventService).should().publishEvent(captor.capture());

			assertThat(captor.getValue().getReviewPk().getLong())
				.isEqualTo(REVIEW_PK.getLong());
		}

		@Test
		void shouldPublishExactlyOneEvent() {
			service.createCustomerReview(RATING, HEADLINE, COMMENT, user, product);

			then(eventService).should(times(1)).publishEvent(any());
		}

		@Test
		void shouldExecuteInOrder() {
			service.createCustomerReview(RATING, HEADLINE, COMMENT, user, product);

			org.mockito.InOrder order = inOrder(review, modelService, eventService);
			order.verify(review).setApprovalStatus(CustomerReviewApprovalType.PENDING);
			order.verify(review).setVerifiedPurchase(false);
			order.verify(modelService).save(review);
			order.verify(eventService).publishEvent(any(VerifiedPurchaseCheckEvent.class));
		}
	}

	@Nested
	class SetEventServiceTest {

		@Test
		void shouldUseInjectedEventService() {
			EventService otherEventService = mock(EventService.class);
			service.setEventService(otherEventService);

			service.createCustomerReview(RATING, HEADLINE, COMMENT, user, product);

			then(otherEventService).should().publishEvent(any(VerifiedPurchaseCheckEvent.class));
			then(eventService).shouldHaveNoInteractions();
		}
	}
}
