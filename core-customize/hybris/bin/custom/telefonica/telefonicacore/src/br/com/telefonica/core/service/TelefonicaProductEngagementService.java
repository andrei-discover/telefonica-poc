package br.com.telefonica.core.service;

import br.com.telefonica.core.model.ProductQuestionModel;
import de.hybris.platform.customerreview.model.CustomerReviewModel;

import java.util.List;
import java.util.Map;


public interface TelefonicaProductEngagementService
{
	Double getAverageRating(List<CustomerReviewModel> reviews);

	Map<Integer, Long> getRatingDistribution(List<CustomerReviewModel> reviews);

	Double getVerifiedPurchasePercentage(List<CustomerReviewModel> reviews);

	List<CustomerReviewModel> getTopHelpfulReviews(List<CustomerReviewModel> reviews, int topN);

	Integer getTotalApprovedQuestions(List<ProductQuestionModel> approvedQuestions);

	Double getQuestionPublicationRate(List<ProductQuestionModel> approvedQuestions, List<ProductQuestionModel> allQuestions);

	Double getAverageAnswerTimeHours(List<ProductQuestionModel> answeredQuestions);
}
