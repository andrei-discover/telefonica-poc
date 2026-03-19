package br.com.telefonica.core.event;

import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.customerreview.model.CustomerReviewModel;
import de.hybris.platform.servicelayer.model.ModelService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class VerifiedPurchaseCheckEventListenerTest {

	@InjectMocks
	private VerifiedPurchaseCheckEventListener listener;

	@Mock
	private ModelService modelService;

	@Mock
	private VerifiedPurchaseCheckEvent event;

	@Mock
	private CustomerReviewModel review;

	@Mock
	private CustomerModel customer;

	@Mock
	private ProductModel product;

	@Mock
	private AbstractOrderModel order;

	@Mock
	private AbstractOrderEntryModel orderEntry;

	private static final PK REVIEW_PK = PK.fromLong(123456789L);

	@BeforeEach
	void setUp() {
		given(event.getReviewPk()).willReturn(REVIEW_PK);
	}

	@Nested
	class OnEventTest {

		@Test
		void shouldGetReviewByPkFromModelService() {
			given(modelService.get(REVIEW_PK)).willReturn(review);
			given(review.getUser()).willReturn(customer);
			given(review.getProduct()).willReturn(product);
			given(customer.getOrders()).willReturn((Collection) Collections.emptyList());

			listener.onEvent(event);

			then(modelService).should().get(REVIEW_PK);
		}

		@Test
		void shouldNotInteractWithReviewWhenReviewIsNull() {
			given(modelService.get(REVIEW_PK)).willReturn(null);

			listener.onEvent(event);

			then(review).shouldHaveNoInteractions();
			then(modelService).should(never()).save(review);
		}

		@Test
		void shouldSaveReviewWhenReviewIsNotNull() {
			given(modelService.get(REVIEW_PK)).willReturn(review);
			given(review.getUser()).willReturn(customer);
			given(review.getProduct()).willReturn(product);
			given(customer.getOrders()).willReturn((Collection) Collections.emptyList());

			listener.onEvent(event);

			then(modelService).should().save(review);
		}

		@Test
		void shouldSetVerifiedPurchaseFalseWhenCustomerHasNoOrders() {
			given(modelService.get(REVIEW_PK)).willReturn(review);
			given(review.getUser()).willReturn(customer);
			given(review.getProduct()).willReturn(product);
			given(customer.getOrders()).willReturn((Collection) Collections.emptyList());

			listener.onEvent(event);

			then(review).should().setVerifiedPurchase(false);
		}

		@Test
		void shouldSetVerifiedPurchaseTrueWhenProductFoundInOrders() {
			given(modelService.get(REVIEW_PK)).willReturn(review);
			given(review.getUser()).willReturn(customer);
			given(review.getProduct()).willReturn(product);
			given(orderEntry.getProduct()).willReturn(product);
			given(order.getEntries()).willReturn((List) Collections.singletonList(orderEntry));
			given(customer.getOrders()).willReturn((Collection) Collections.singletonList(order));

			listener.onEvent(event);

			then(review).should().setVerifiedPurchase(true);
		}

		@Test
		void shouldSetVerifiedPurchaseFalseWhenProductNotFoundInOrders() {
			ProductModel otherProduct = mock(ProductModel.class);
			given(modelService.get(REVIEW_PK)).willReturn(review);
			given(review.getUser()).willReturn(customer);
			given(review.getProduct()).willReturn(product);
			given(orderEntry.getProduct()).willReturn(otherProduct);
			given(order.getEntries()).willReturn((List) Collections.singletonList(orderEntry));
			given(customer.getOrders()).willReturn((Collection) Collections.singletonList(order));

			listener.onEvent(event);

			then(review).should().setVerifiedPurchase(false);
		}
	}

	@Nested
	class IsVerifiedPurchaseTest {

		@Test
		void shouldReturnFalseWhenUserIsNotCustomerModel() {
			UserModel plainUser = mock(UserModel.class);

			boolean result = listener.isVerifiedPurchase(plainUser, product);

			assertThat(result).isFalse();
		}

		@Test
		void shouldReturnFalseWhenCustomerOrdersIsNull() {
			given(customer.getOrders()).willReturn(null);

			boolean result = listener.isVerifiedPurchase(customer, product);

			assertThat(result).isFalse();
		}

		@Test
		void shouldReturnFalseWhenCustomerHasNoOrders() {
			given(customer.getOrders()).willReturn((Collection) Collections.emptyList());

			boolean result = listener.isVerifiedPurchase(customer, product);

			assertThat(result).isFalse();
		}

		@Test
		void shouldReturnTrueWhenProductExistsInOrderEntries() {
			given(orderEntry.getProduct()).willReturn(product);
			given(order.getEntries()).willReturn((List) Collections.singletonList(orderEntry));
			given(customer.getOrders()).willReturn((Collection) Collections.singletonList(order));

			boolean result = listener.isVerifiedPurchase(customer, product);

			assertThat(result).isTrue();
		}

		@Test
		void shouldReturnFalseWhenProductNotInAnyOrderEntry() {
			ProductModel otherProduct = mock(ProductModel.class);
			given(orderEntry.getProduct()).willReturn(otherProduct);
			given(order.getEntries()).willReturn((List) Collections.singletonList(orderEntry));
			given(customer.getOrders()).willReturn((Collection) Collections.singletonList(order));

			boolean result = listener.isVerifiedPurchase(customer, product);

			assertThat(result).isFalse();
		}

		@Test
		void shouldReturnTrueWhenProductFoundInOneOfMultipleOrders() {
			AbstractOrderModel otherOrder = mock(AbstractOrderModel.class);
			AbstractOrderEntryModel matchingEntry = mock(AbstractOrderEntryModel.class);
			AbstractOrderEntryModel nonMatchingEntry = mock(AbstractOrderEntryModel.class);
			ProductModel differentProduct = mock(ProductModel.class);

			given(nonMatchingEntry.getProduct()).willReturn(differentProduct);
			given(order.getEntries()).willReturn((List) Collections.singletonList(nonMatchingEntry));

			given(matchingEntry.getProduct()).willReturn(product);
			given(otherOrder.getEntries()).willReturn((List) Collections.singletonList(matchingEntry));

			given(customer.getOrders()).willReturn((Collection) Arrays.asList(order, otherOrder));

			boolean result = listener.isVerifiedPurchase(customer, product);

			assertThat(result).isTrue();
		}

		@Test
		void shouldIgnoreNullOrdersInList() {
			given(customer.getOrders()).willReturn((Collection) Collections.singletonList(null));

			boolean result = listener.isVerifiedPurchase(customer, product);

			assertThat(result).isFalse();
		}
	}

	@Nested
	class SetModelServiceTest {

		@Test
		void shouldUseInjectedModelService() {
			ModelService otherModelService = mock(ModelService.class);
			given(otherModelService.get(REVIEW_PK)).willReturn(review);
			given(review.getUser()).willReturn(customer);
			given(review.getProduct()).willReturn(product);
			given(customer.getOrders()).willReturn((Collection) Collections.emptyList());

			listener.setModelService(otherModelService);
			listener.onEvent(event);

			then(otherModelService).should().get(REVIEW_PK);
			then(modelService).shouldHaveNoInteractions();
		}
	}
}
