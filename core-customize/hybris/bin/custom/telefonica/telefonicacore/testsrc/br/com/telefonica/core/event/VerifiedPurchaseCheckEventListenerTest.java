package br.com.telefonica.core.event;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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
	private VerifiedPurchaseCheckEvent event;
	private CustomerReviewModel review;
	private CustomerModel customer;
	private ProductModel product;
	private OrderModel order;
	private AbstractOrderEntryModel orderEntry;

	private static final PK REVIEW_PK = PK.fromLong(123456789L);

	@Before
	public void setUp() {
		listener     = new VerifiedPurchaseCheckEventListener();
		modelService = mock(ModelService.class);
		event        = mock(VerifiedPurchaseCheckEvent.class);
		review       = mock(CustomerReviewModel.class);
		customer     = mock(CustomerModel.class);
		product      = mock(ProductModel.class);
		order        = mock(OrderModel.class);
		orderEntry   = mock(AbstractOrderEntryModel.class);

		listener.setModelService(modelService);
		when(event.getReviewPk()).thenReturn(REVIEW_PK);
	}

	@Test
	public void onEvent_ShouldGetReviewByPk_WhenEventIsReceived() {
		when(modelService.get(REVIEW_PK)).thenReturn(review);
		when(review.getUser()).thenReturn(customer);
		when(review.getProduct()).thenReturn(product);
		when(customer.getOrders()).thenReturn((Collection) Collections.emptyList());

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
		when(customer.getOrders()).thenReturn((Collection) Collections.emptyList());

		listener.onEvent(event);

		verify(modelService, times(1)).save(review);
	}

	@Test
	public void onEvent_ShouldSetVerifiedPurchaseFalse_WhenCustomerHasNoOrders() {
		when(modelService.get(REVIEW_PK)).thenReturn(review);
		when(review.getUser()).thenReturn(customer);
		when(review.getProduct()).thenReturn(product);
		when(customer.getOrders()).thenReturn((Collection) Collections.emptyList());

		listener.onEvent(event);

		verify(review).setVerifiedPurchase(false);
	}

	@Test
	public void onEvent_ShouldSetVerifiedPurchaseTrue_WhenProductFoundInOrders() {
		when(modelService.get(REVIEW_PK)).thenReturn(review);
		when(review.getUser()).thenReturn(customer);
		when(review.getProduct()).thenReturn(product);
		when(orderEntry.getProduct()).thenReturn(product);
		when(order.getEntries()).thenReturn((List) Collections.singletonList(orderEntry));
		when(customer.getOrders()).thenReturn((Collection) Collections.singletonList(order));

		listener.onEvent(event);

		verify(review).setVerifiedPurchase(true);
	}

	@Test
	public void onEvent_ShouldSetVerifiedPurchaseFalse_WhenProductNotFoundInOrders() {
		ProductModel otherProduct = mock(ProductModel.class);
		when(modelService.get(REVIEW_PK)).thenReturn(review);
		when(review.getUser()).thenReturn(customer);
		when(review.getProduct()).thenReturn(product);
		when(orderEntry.getProduct()).thenReturn(otherProduct);
		when(order.getEntries()).thenReturn((List) Collections.singletonList(orderEntry));
		when(customer.getOrders()).thenReturn((Collection) Collections.singletonList(order));

		listener.onEvent(event);

		verify(review).setVerifiedPurchase(false);
	}

	@Test
	public void isVerifiedPurchase_ShouldReturnFalse_WhenUserIsNotCustomerModel() {
		UserModel plainUser = mock(UserModel.class);

		boolean result = listener.isVerifiedPurchase(plainUser, product);

		Assert.assertFalse(result);
	}

	@Test
	public void isVerifiedPurchase_ShouldReturnFalse_WhenCustomerOrdersIsNull() {
		when(customer.getOrders()).thenReturn(null);

		boolean result = listener.isVerifiedPurchase(customer, product);

		Assert.assertFalse(result);
	}

	@Test
	public void isVerifiedPurchase_ShouldReturnFalse_WhenCustomerHasNoOrders() {
		when(customer.getOrders()).thenReturn((Collection) Collections.emptyList());

		boolean result = listener.isVerifiedPurchase(customer, product);

		Assert.assertFalse(result);
	}

	@Test
	public void isVerifiedPurchase_ShouldReturnTrue_WhenProductExistsInOrderEntries() {
		when(orderEntry.getProduct()).thenReturn(product);
		when(order.getEntries()).thenReturn((List) Collections.singletonList(orderEntry));
		when(customer.getOrders()).thenReturn((Collection) Collections.singletonList(order));

		boolean result = listener.isVerifiedPurchase(customer, product);

		Assert.assertTrue(result);
	}

	@Test
	public void isVerifiedPurchase_ShouldReturnFalse_WhenProductNotInAnyOrderEntry() {
		ProductModel otherProduct = mock(ProductModel.class);
		when(orderEntry.getProduct()).thenReturn(otherProduct);
		when(order.getEntries()).thenReturn((List) Collections.singletonList(orderEntry));
		when(customer.getOrders()).thenReturn((Collection) Collections.singletonList(order));

		boolean result = listener.isVerifiedPurchase(customer, product);

		Assert.assertFalse(result);
	}

	@Test
	public void isVerifiedPurchase_ShouldReturnTrue_WhenProductFoundInOneOfMultipleOrders() {
		OrderModel otherOrder                    = mock(OrderModel.class);
		AbstractOrderEntryModel matchingEntry    = mock(AbstractOrderEntryModel.class);
		AbstractOrderEntryModel nonMatchingEntry = mock(AbstractOrderEntryModel.class);
		ProductModel differentProduct            = mock(ProductModel.class);

		when(nonMatchingEntry.getProduct()).thenReturn(differentProduct);
		when(order.getEntries()).thenReturn((List) Collections.singletonList(nonMatchingEntry));

		when(matchingEntry.getProduct()).thenReturn(product);
		when(otherOrder.getEntries()).thenReturn((List) Collections.singletonList(matchingEntry));

		when(customer.getOrders()).thenReturn((Collection) Arrays.asList(order, otherOrder));

		boolean result = listener.isVerifiedPurchase(customer, product);

		Assert.assertTrue(result);
	}

	@Test
	public void isVerifiedPurchase_ShouldIgnoreNullOrders_WhenListContainsNull() {
		when(customer.getOrders()).thenReturn((Collection) Collections.singletonList(null));

		boolean result = listener.isVerifiedPurchase(customer, product);

		Assert.assertFalse(result);
	}

	@Test
	public void setModelService_ShouldUseNewModelService_WhenReplaced() {
		ModelService otherModelService = mock(ModelService.class);
		when(otherModelService.get(REVIEW_PK)).thenReturn(review);
		when(review.getUser()).thenReturn(customer);
		when(review.getProduct()).thenReturn(product);
		when(customer.getOrders()).thenReturn((Collection) Collections.emptyList());

		listener.setModelService(otherModelService);
		listener.onEvent(event);

		verify(otherModelService).get(REVIEW_PK);
		verify(modelService, never()).get(REVIEW_PK);
	}
}
