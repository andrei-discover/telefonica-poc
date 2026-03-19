package br.com.telefonica.core.service;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.customerreview.CustomerReviewService;
import de.hybris.platform.customerreview.model.CustomerReviewModel;


public interface TelefonicaCustomerReviewService extends CustomerReviewService {

	CustomerReviewModel createCustomerReview(Double var1, String var2, String var3, UserModel var4, ProductModel var5);

}
