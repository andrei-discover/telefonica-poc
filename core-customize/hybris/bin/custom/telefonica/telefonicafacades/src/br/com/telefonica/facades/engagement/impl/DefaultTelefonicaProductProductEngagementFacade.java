package br.com.telefonica.facades.engagement.impl;

import br.com.telefonica.core.dao.TelefonicaProductEngagementDao;
import br.com.telefonica.core.model.ProductQuestionModel;
import br.com.telefonica.core.service.TelefonicaProductEngagementService;
import br.com.telefonica.facades.engagement.TelefonicaProductEngagementFacade;
import br.com.telefonica.facades.product.engagement.ProductEngagementSummaryData;
import de.hybris.platform.commercefacades.product.data.ReviewData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.customerreview.model.CustomerReviewModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


public class DefaultTelefonicaProductProductEngagementFacade implements TelefonicaProductEngagementFacade
{
	private static final Logger LOG = LoggerFactory.getLogger(DefaultTelefonicaProductProductEngagementFacade.class);

	private static final int DEFAULT_TOP_N = 5;

	private TelefonicaProductEngagementDao telefonicaProductEngagementDao;
	private TelefonicaProductEngagementService telefonicaProductEngagementService;
	private ProductService productService;
	private Converter<CustomerReviewModel, ReviewData> customerReviewConverter;

	@Override
	public ProductEngagementSummaryData getEngagementSummary(final String productCode, final int topN)
	{
		final ProductModel product = productService.getProductForCode(productCode);
		final int effectiveTopN = topN > 0 ? topN : DEFAULT_TOP_N;

		final List<CustomerReviewModel> approvedReviews = telefonicaProductEngagementDao.findApprovedReviews(product);
		final List<ProductQuestionModel> approvedQuestions = telefonicaProductEngagementDao.findApprovedQuestions(product);
		final List<ProductQuestionModel> allQuestions = telefonicaProductEngagementDao.findAllQuestions(product);
		final List<ProductQuestionModel> answeredQuestions = telefonicaProductEngagementDao.findAnsweredQuestions(product);

		final ProductEngagementSummaryData summary = new ProductEngagementSummaryData();
		summary.setAverageRating(telefonicaProductEngagementService.getAverageRating(approvedReviews));
		summary.setRatingDistribution(telefonicaProductEngagementService.getRatingDistribution(approvedReviews));
		summary.setVerifiedPurchasePercentage(telefonicaProductEngagementService.getVerifiedPurchasePercentage(approvedReviews));
		summary.setTopHelpfulReviews(convertReviews(
			telefonicaProductEngagementService.getTopHelpfulReviews(approvedReviews, effectiveTopN)));
		summary.setTotalQuestions(telefonicaProductEngagementService.getTotalApprovedQuestions(approvedQuestions));
		summary.setQuestionPublicationRate(
			telefonicaProductEngagementService.getQuestionPublicationRate(approvedQuestions, allQuestions));
		summary.setAverageAnswerTimeHours(telefonicaProductEngagementService.getAverageAnswerTimeHours(answeredQuestions));

		LOG.info("Engagement summary generated for product: {}", productCode);

		return summary;
	}

	private List<ReviewData> convertReviews(final List<CustomerReviewModel> reviews)
	{
		return customerReviewConverter.convertAll(reviews);
	}

	public void setTelefonicaProductEngagementDao(TelefonicaProductEngagementDao telefonicaProductEngagementDao)
	{
		this.telefonicaProductEngagementDao = telefonicaProductEngagementDao;
	}

	public void setTelefonicaProductEngagementService(TelefonicaProductEngagementService telefonicaProductEngagementService)
	{
		this.telefonicaProductEngagementService = telefonicaProductEngagementService;
	}

	public void setProductService(ProductService productService)
	{
		this.productService = productService;
	}

	public void setCustomerReviewConverter(Converter<CustomerReviewModel, ReviewData> customerReviewConverter)
	{
		this.customerReviewConverter = customerReviewConverter;
	}
}
