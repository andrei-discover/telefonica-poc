package br.com.telefonica.core.dao;

import br.com.telefonica.core.model.ProductQuestionModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.customerreview.model.CustomerReviewModel;

import java.util.List;


public interface TelefonicaProductEngagementDao
{
	List<CustomerReviewModel> findApprovedReviews(ProductModel product);

	List<ProductQuestionModel> findApprovedQuestions(ProductModel product);

	List<ProductQuestionModel> findAllQuestions(ProductModel product);

	List<ProductQuestionModel> findAnsweredQuestions(ProductModel product);
}
