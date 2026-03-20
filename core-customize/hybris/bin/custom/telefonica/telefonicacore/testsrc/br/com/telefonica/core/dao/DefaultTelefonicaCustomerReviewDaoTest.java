package br.com.telefonica.core.dao;

import br.com.telefonica.core.dao.impl.DefaultTelefonicaCustomerReviewDao;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultTelefonicaCustomerReviewDaoTest {

	private DefaultTelefonicaCustomerReviewDao dao;
	private FlexibleSearchService flexibleSearchService;
	private CustomerModel customer;
	private ProductModel product;

	@Before
	public void setUp() {
		dao                   = new DefaultTelefonicaCustomerReviewDao();
		flexibleSearchService = mock(FlexibleSearchService.class);
		customer              = mock(CustomerModel.class);
		product               = mock(ProductModel.class);

		dao.setFlexibleSearchService(flexibleSearchService);
	}

	private SearchResult<Integer> mockSearchResult(final Integer count) {
		final SearchResult<Integer> searchResult = mock(SearchResult.class);
		when(searchResult.getResult()).thenReturn(Collections.singletonList(count));
		when(flexibleSearchService.search(any(FlexibleSearchQuery.class)))
			.thenReturn((SearchResult) searchResult);
		return searchResult;
	}

	@Test
	public void hasCustomerPurchasedProduct_ShouldReturnTrue_WhenCountIsGreaterThanZero() {
		mockSearchResult(3);

		boolean result = dao.hasCustomerPurchasedProduct(customer, product);

		Assert.assertTrue(result);
	}

	@Test
	public void hasCustomerPurchasedProduct_ShouldReturnTrue_WhenCountIsOne() {
		mockSearchResult(1);

		boolean result = dao.hasCustomerPurchasedProduct(customer, product);

		Assert.assertTrue(result);
	}

	@Test
	public void hasCustomerPurchasedProduct_ShouldReturnFalse_WhenCountIsZero() {
		mockSearchResult(0);

		boolean result = dao.hasCustomerPurchasedProduct(customer, product);

		Assert.assertFalse(result);
	}

	@Test
	public void hasCustomerPurchasedProduct_ShouldReturnFalse_WhenCountIsNull() {
		mockSearchResult(null);

		boolean result = dao.hasCustomerPurchasedProduct(customer, product);

		Assert.assertFalse(result);
	}

	@Test
	public void hasCustomerPurchasedProduct_ShouldCallFlexibleSearchService_WhenCalled() {
		mockSearchResult(0);

		dao.hasCustomerPurchasedProduct(customer, product);

		verify(flexibleSearchService).search(any(FlexibleSearchQuery.class));
	}

	@Test
	public void hasCustomerPurchasedProduct_ShouldPassCorrectParamsToQuery_WhenCalled() {
		mockSearchResult(0);

		dao.hasCustomerPurchasedProduct(customer, product);

		final ArgumentCaptor<FlexibleSearchQuery> captor =
			ArgumentCaptor.forClass(FlexibleSearchQuery.class);
		verify(flexibleSearchService).search(captor.capture());

		final FlexibleSearchQuery query = captor.getValue();
		Assert.assertEquals(customer, query.getQueryParameters().get("customer"));
		Assert.assertEquals(product,  query.getQueryParameters().get("product"));
		Assert.assertEquals(OrderStatus.COMPLETED, query.getQueryParameters().get("status"));
	}

	@Test
	public void hasCustomerPurchasedProduct_ShouldUseResultClassListWithInteger_WhenCalled() {
		mockSearchResult(0);

		dao.hasCustomerPurchasedProduct(customer, product);

		final ArgumentCaptor<FlexibleSearchQuery> captor =
			ArgumentCaptor.forClass(FlexibleSearchQuery.class);
		verify(flexibleSearchService).search(captor.capture());

		Assert.assertTrue(
			captor.getValue().getResultClassList().contains(Integer.class));
	}

	@Test
	public void setFlexibleSearchService_ShouldUseNewService_WhenReplaced() {
		final FlexibleSearchService otherService = mock(FlexibleSearchService.class);
		final SearchResult<Integer> searchResult = mock(SearchResult.class);
		when(searchResult.getResult()).thenReturn(Collections.singletonList(1));
		when(otherService.search(any(FlexibleSearchQuery.class)))
			.thenReturn((SearchResult) searchResult);

		dao.setFlexibleSearchService(otherService);
		dao.hasCustomerPurchasedProduct(customer, product);

		verify(otherService).search(any(FlexibleSearchQuery.class));
		verify(flexibleSearchService, org.mockito.Mockito.never())
			.search(any(FlexibleSearchQuery.class));
	}
}
