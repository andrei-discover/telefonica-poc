package br.com.telefonica.core.dao.impl;

import br.com.telefonica.core.dao.TelefonicaProductEngagementDao;
import br.com.telefonica.core.enums.QuestionStatus;
import br.com.telefonica.core.model.ProductQuestionModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.customerreview.enums.CustomerReviewApprovalType;
import de.hybris.platform.customerreview.model.CustomerReviewModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import org.apache.commons.collections.CollectionUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DefaultTelefonicaProductEngagementDao implements TelefonicaProductEngagementDao
{
	private static final String FIND_APPROVED_REVIEWS =
		"SELECT {r:pk} FROM {CustomerReview AS r} WHERE {r:product} = ?product AND {r:approvalStatus} = ?status";

	private static final String FIND_APPROVED_QUESTIONS =
		"SELECT {q:pk} FROM {ProductQuestion AS q} WHERE {q:product} = ?product AND {q:status} = ?status";

	private static final String FIND_ALL_QUESTIONS = "SELECT {q:pk} FROM {ProductQuestion AS q} WHERE {q:product} = ?product";

	private static final String FIND_ANSWERED_QUESTIONS =
		"SELECT {q:pk} FROM {ProductQuestion AS q} WHERE {q:product} = ?product AND {q:status} = ?status "
			+ "AND {q:answerModeratedAt} IS NOT NULL";

	private FlexibleSearchService flexibleSearchService;

	@Override
	public List<CustomerReviewModel> findApprovedReviews(final ProductModel product)
	{
		final Map<String, Object> params = new HashMap<>();
		params.put("product", product);
		params.put("status", CustomerReviewApprovalType.APPROVED);

		final FlexibleSearchQuery query = new FlexibleSearchQuery(FIND_APPROVED_REVIEWS, params);
		final SearchResult<CustomerReviewModel> result = flexibleSearchService.search(query);

		return CollectionUtils.isNotEmpty(result.getResult()) ? result.getResult() : Collections.emptyList();
	}

	@Override
	public List<ProductQuestionModel> findApprovedQuestions(final ProductModel product)
	{
		final Map<String, Object> params = new HashMap<>();
		params.put("product", product);
		params.put("status", QuestionStatus.APPROVED);

		final FlexibleSearchQuery query = new FlexibleSearchQuery(FIND_APPROVED_QUESTIONS, params);
		final SearchResult<ProductQuestionModel> result = flexibleSearchService.search(query);

		return CollectionUtils.isNotEmpty(result.getResult()) ? result.getResult() : Collections.emptyList();
	}

	@Override
	public List<ProductQuestionModel> findAllQuestions(final ProductModel product)
	{
		final Map<String, Object> params = new HashMap<>();
		params.put("product", product);

		final FlexibleSearchQuery query = new FlexibleSearchQuery(FIND_ALL_QUESTIONS, params);
		final SearchResult<ProductQuestionModel> result = flexibleSearchService.search(query);

		return CollectionUtils.isNotEmpty(result.getResult()) ? result.getResult() : Collections.emptyList();
	}

	@Override
	public List<ProductQuestionModel> findAnsweredQuestions(final ProductModel product)
	{
		final Map<String, Object> params = new HashMap<>();
		params.put("product", product);
		params.put("status", QuestionStatus.APPROVED);

		final FlexibleSearchQuery query = new FlexibleSearchQuery(FIND_ANSWERED_QUESTIONS, params);
		final SearchResult<ProductQuestionModel> result = flexibleSearchService.search(query);

		return CollectionUtils.isNotEmpty(result.getResult()) ? result.getResult() : Collections.emptyList();
	}

	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
	}
}
