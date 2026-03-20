package br.com.telefonica.core.dao;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.CustomerModel;


public interface TelefonicaCustomerReviewDao
{
	boolean hasCustomerPurchasedProduct(CustomerModel customer, ProductModel product);
}
