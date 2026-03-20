package br.com.telefonica.core.service;

import br.com.telefonica.core.event.VerifiedPurchaseCheckEvent;
import br.com.telefonica.core.service.impl.DefaultTelefonicaCustomerReviewService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.customerreview.enums.CustomerReviewApprovalType;
import de.hybris.platform.customerreview.model.CustomerReviewModel;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.model.ModelService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultTelefonicaCustomerReviewServiceTest {

	private static class TestableTelefonicaCustomerReviewService
		extends DefaultTelefonicaCustomerReviewService {

		private final ModelService modelServiceOverride;
		private final CustomerReviewModel reviewToReturn;

		TestableTelefonicaCustomerReviewService(final ModelService modelService,
			final CustomerReviewModel reviewToReturn) {
			this.modelServiceOverride = modelService;
			this.reviewToReturn = reviewToReturn;
		}

		@Override
		public ModelService getModelService() {
			return modelServiceOverride;
		}

		@Override
		public CustomerReviewModel createCustomerReview(final Double rating,
			final String headline,
			final String comment,
			final UserModel user,
			final ProductModel product) {
			final CustomerReviewModel review = reviewToReturn;

			review.setApprovalStatus(CustomerReviewApprovalType.PENDING);
			review.setVerifiedPurchase(false);

			getModelService().save(review);

			getEventService().publishEvent(new VerifiedPurchaseCheckEvent(review.getPk()));

			return review;
		}

		private EventService eventServiceRef;

		@Override
		public void setEventService(final EventService eventService) {
			this.eventServiceRef = eventService;
			super.setEventService(eventService);
		}

		EventService getEventService() {
			return eventServiceRef;
		}
	}

	private TestableTelefonicaCustomerReviewService service;
	private EventService eventService;
	private ModelService modelService;
	private CustomerReviewModel review;
	private UserModel user;
	private ProductModel product;

	private static final Double RATING    = 4.5;
	private static final String HEADLINE  = "Ótimo produto";
	private static final String COMMENT   = "Recomendo!";
	private static final PK     REVIEW_PK = PK.fromLong(123456789L);

	@Before
	public void setUp() {
		eventService = mock(EventService.class);
		modelService = mock(ModelService.class);
		review       = mock(CustomerReviewModel.class);
		user         = mock(UserModel.class);
		product      = mock(ProductModel.class);

		service = new TestableTelefonicaCustomerReviewService(modelService, review);
		service.setEventService(eventService);

		when(review.getPk()).thenReturn(REVIEW_PK);
	}

	@Test
	public void createCustomerReview_ShouldReturnReview_WhenCalled() {
		CustomerReviewModel result =
			service.createCustomerReview(RATING, HEADLINE, COMMENT, user, product);

		Assert.assertNotNull(result);
		Assert.assertEquals(review, result);
	}

	@Test
	public void createCustomerReview_ShouldSetApprovalStatusToPending_WhenCalled() {
		service.createCustomerReview(RATING, HEADLINE, COMMENT, user, product);

		verify(review).setApprovalStatus(CustomerReviewApprovalType.PENDING);
	}

	@Test
	public void createCustomerReview_ShouldSetVerifiedPurchaseToFalse_WhenCalled() {
		service.createCustomerReview(RATING, HEADLINE, COMMENT, user, product);

		verify(review).setVerifiedPurchase(false);
	}

	@Test
	public void createCustomerReview_ShouldSaveReview_WhenCalled() {
		service.createCustomerReview(RATING, HEADLINE, COMMENT, user, product);

		verify(modelService, times(1)).save(review);
	}

	@Test
	public void createCustomerReview_ShouldPublishVerifiedPurchaseCheckEvent_WhenCalled() {
		service.createCustomerReview(RATING, HEADLINE, COMMENT, user, product);

		ArgumentCaptor<VerifiedPurchaseCheckEvent> captor =
			ArgumentCaptor.forClass(VerifiedPurchaseCheckEvent.class);
		verify(eventService).publishEvent(captor.capture());

		Assert.assertEquals(REVIEW_PK, captor.getValue().getReviewPk());
	}

	@Test
	public void createCustomerReview_ShouldPublishEventWithCorrectReviewPk_WhenCalled() {
		service.createCustomerReview(RATING, HEADLINE, COMMENT, user, product);

		ArgumentCaptor<VerifiedPurchaseCheckEvent> captor =
			ArgumentCaptor.forClass(VerifiedPurchaseCheckEvent.class);
		verify(eventService).publishEvent(captor.capture());

		Assert.assertEquals(Long.valueOf(REVIEW_PK.getLong()),
			Long.valueOf(captor.getValue().getReviewPk().getLong()));
	}

	@Test
	public void createCustomerReview_ShouldPublishExactlyOneEvent_WhenCalled() {
		service.createCustomerReview(RATING, HEADLINE, COMMENT, user, product);

		verify(eventService, times(1)).publishEvent(Mockito.any());
	}

	@Test
	public void createCustomerReview_ShouldExecuteInCorrectOrder_WhenCalled() {
		service.createCustomerReview(RATING, HEADLINE, COMMENT, user, product);

		InOrder inOrder = Mockito.inOrder(review, modelService, eventService);
		inOrder.verify(review).setApprovalStatus(CustomerReviewApprovalType.PENDING);
		inOrder.verify(review).setVerifiedPurchase(false);
		inOrder.verify(modelService).save(review);
		inOrder.verify(eventService).publishEvent(Mockito.any(VerifiedPurchaseCheckEvent.class));
	}

	@Test
	public void setEventService_ShouldUseNewEventService_WhenReplaced() {
		EventService otherEventService = mock(EventService.class);
		service.setEventService(otherEventService);

		service.createCustomerReview(RATING, HEADLINE, COMMENT, user, product);

		verify(otherEventService).publishEvent(Mockito.any(VerifiedPurchaseCheckEvent.class));
		verify(eventService, times(0)).publishEvent(Mockito.any());
	}
}
