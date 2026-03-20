package br.com.telefonica.core.dao.impl;

import br.com.telefonica.core.dao.TelefonicaCustomerReviewDao;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class DefaultTelefonicaCustomerReviewDao implements TelefonicaCustomerReviewDao {

	private FlexibleSearchService flexibleSearchService;

	private static final String VERIFIED_PURCHASE_QUERY =
		"SELECT COUNT({oe:pk}) " +
			"FROM {OrderEntry AS oe " +
			"JOIN Order AS o ON {oe:order} = {o:pk} " +
			"JOIN Customer AS c ON {o:user} = {c:pk}} " +
			"WHERE {c:pk} = ?customer " +
			"AND {oe:product} = ?product " +
			"AND {o:status} = ?status";


	@Override
	public boolean hasCustomerPurchasedProduct(final CustomerModel customer, final ProductModel product) {
		final Map<String, Object> params = new HashMap<>();
		params.put("customer", customer);
		params.put("product",  product);
		params.put("status",   OrderStatus.COMPLETED);

		final FlexibleSearchQuery query = new FlexibleSearchQuery(VERIFIED_PURCHASE_QUERY, params);
		query.setResultClassList(Arrays.asList(Integer.class));

		final Integer count = (Integer) flexibleSearchService
			.search(query)
			.getResult()
			.get(0);

		return count != null && count > 0;
	}

	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService) {
		this.flexibleSearchService = flexibleSearchService;
	}

}
