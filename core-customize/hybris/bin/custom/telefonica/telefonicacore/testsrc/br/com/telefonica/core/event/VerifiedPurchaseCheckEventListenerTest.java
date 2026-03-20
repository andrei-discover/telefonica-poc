package br.com.telefonica.core.event;

import br.com.telefonica.core.dao.TelefonicaCustomerReviewDao;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.customerreview.model.CustomerReviewModel;
import de.hybris.platform.servicelayer.model.ModelService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class VerifiedPurchaseCheckEventListenerTest {

	private VerifiedPurchaseCheckEventListener listener;
	private ModelService modelService;
	private TelefonicaCustomerReviewDao telefonicaCustomerReviewDao;
	private VerifiedPurchaseCheckEvent event;
	private CustomerReviewModel review;
	private CustomerModel customer;
	private ProductModel product;

	private static final PK REVIEW_PK = PK.fromLong(123456789L);

	@Before
	public void setUp() {
		listener                    = new VerifiedPurchaseCheckEventListener();
		modelService                = mock(ModelService.class);
		telefonicaCustomerReviewDao = mock(TelefonicaCustomerReviewDao.class);
		event                       = mock(VerifiedPurchaseCheckEvent.class);
		review                      = mock(CustomerReviewModel.class);
		customer                    = mock(CustomerModel.class);
		product                     = mock(ProductModel.class);

		listener.setModelService(modelService);
		listener.setTelefonicaCustomerReviewDao(telefonicaCustomerReviewDao);
		when(event.getReviewPk()).thenReturn(REVIEW_PK);
	}

	@Test
	public void onEvent_ShouldGetReviewByPk_WhenEventIsReceived() {
		when(modelService.get(REVIEW_PK)).thenReturn(review);
		when(review.getUser()).thenReturn(customer);
		when(review.getProduct()).thenReturn(product);
		when(telefonicaCustomerReviewDao.hasCustomerPurchasedProduct(customer, product))
			.thenReturn(false);

		listener.onEvent(event);

		verify(modelService).get(REVIEW_PK);
	}

	@Test
	public void onEvent_ShouldNotSaveReview_WhenReviewIsNull() {
		when(modelService.get(REVIEW_PK)).thenReturn(null);

		listener.onEvent(event);

		verify(modelService, never()).save(review);
	}

	@Test
	public void onEvent_ShouldSaveReview_WhenReviewIsNotNull() {
		when(modelService.get(REVIEW_PK)).thenReturn(review);
		when(review.getUser()).thenReturn(customer);
		when(review.getProduct()).thenReturn(product);
		when(telefonicaCustomerReviewDao.hasCustomerPurchasedProduct(customer, product))
			.thenReturn(false);

		listener.onEvent(event);

		verify(modelService, times(1)).save(review);
	}

	@Test
	public void onEvent_ShouldSetVerifiedPurchaseTrue_WhenDaoReturnsTrue() {
		when(modelService.get(REVIEW_PK)).thenReturn(review);
		when(review.getUser()).thenReturn(customer);
		when(review.getProduct()).thenReturn(product);
		when(telefonicaCustomerReviewDao.hasCustomerPurchasedProduct(customer, product))
			.thenReturn(true);

		listener.onEvent(event);

		verify(review).setVerifiedPurchase(true);
	}

	@Test
	public void onEvent_ShouldSetVerifiedPurchaseFalse_WhenDaoReturnsFalse() {
		when(modelService.get(REVIEW_PK)).thenReturn(review);
		when(review.getUser()).thenReturn(customer);
		when(review.getProduct()).thenReturn(product);
		when(telefonicaCustomerReviewDao.hasCustomerPurchasedProduct(customer, product))
			.thenReturn(false);

		listener.onEvent(event);

		verify(review).setVerifiedPurchase(false);
	}

	@Test
	public void onEvent_ShouldDelegateToDaoWithCorrectParams_WhenReviewIsNotNull() {
		when(modelService.get(REVIEW_PK)).thenReturn(review);
		when(review.getUser()).thenReturn(customer);
		when(review.getProduct()).thenReturn(product);
		when(telefonicaCustomerReviewDao.hasCustomerPurchasedProduct(customer, product))
			.thenReturn(false);

		listener.onEvent(event);

		verify(telefonicaCustomerReviewDao).hasCustomerPurchasedProduct(customer, product);
	}

	@Test
	public void isVerifiedPurchase_ShouldReturnFalse_WhenUserIsNotCustomerModel() {
		UserModel plainUser = mock(UserModel.class);

		boolean result = listener.isVerifiedPurchase(plainUser, product);

		Assert.assertFalse(result);
		verify(telefonicaCustomerReviewDao, never())
			.hasCustomerPurchasedProduct(customer, product);
	}

	@Test
	public void isVerifiedPurchase_ShouldReturnTrue_WhenDaoReturnsTrue() {
		when(telefonicaCustomerReviewDao.hasCustomerPurchasedProduct(customer, product))
			.thenReturn(true);

		boolean result = listener.isVerifiedPurchase(customer, product);

		Assert.assertTrue(result);
	}

	@Test
	public void isVerifiedPurchase_ShouldReturnFalse_WhenDaoReturnsFalse() {
		when(telefonicaCustomerReviewDao.hasCustomerPurchasedProduct(customer, product))
			.thenReturn(false);

		boolean result = listener.isVerifiedPurchase(customer, product);

		Assert.assertFalse(result);
	}

	@Test
	public void isVerifiedPurchase_ShouldDelegateToDaoWithCorrectParams_WhenUserIsCustomer() {
		when(telefonicaCustomerReviewDao.hasCustomerPurchasedProduct(customer, product))
			.thenReturn(false);

		listener.isVerifiedPurchase(customer, product);

		verify(telefonicaCustomerReviewDao).hasCustomerPurchasedProduct(customer, product);
	}

	@Test
	public void setModelService_ShouldUseNewModelService_WhenReplaced() {
		ModelService otherModelService = mock(ModelService.class);
		when(otherModelService.get(REVIEW_PK)).thenReturn(review);
		when(review.getUser()).thenReturn(customer);
		when(review.getProduct()).thenReturn(product);
		when(telefonicaCustomerReviewDao.hasCustomerPurchasedProduct(customer, product))
			.thenReturn(false);

		listener.setModelService(otherModelService);
		listener.onEvent(event);

		verify(otherModelService).get(REVIEW_PK);
		verify(modelService, never()).get(REVIEW_PK);
	}

	@Test
	public void setTelefonicaCustomerReviewDao_ShouldUseNewDao_WhenReplaced() {
		TelefonicaCustomerReviewDao otherDao = mock(TelefonicaCustomerReviewDao.class);
		when(modelService.get(REVIEW_PK)).thenReturn(review);
		when(review.getUser()).thenReturn(customer);
		when(review.getProduct()).thenReturn(product);
		when(otherDao.hasCustomerPurchasedProduct(customer, product)).thenReturn(true);

		listener.setTelefonicaCustomerReviewDao(otherDao);
		listener.onEvent(event);

		verify(otherDao).hasCustomerPurchasedProduct(customer, product);
		verify(telefonicaCustomerReviewDao, never())
			.hasCustomerPurchasedProduct(customer, product);
	}
}
