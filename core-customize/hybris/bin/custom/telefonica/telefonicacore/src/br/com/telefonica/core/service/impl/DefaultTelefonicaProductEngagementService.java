package br.com.telefonica.core.service.impl;

import br.com.telefonica.core.model.ProductQuestionModel;
import br.com.telefonica.core.service.TelefonicaProductEngagementService;
import de.hybris.platform.customerreview.model.CustomerReviewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


public class DefaultTelefonicaProductEngagementService implements TelefonicaProductEngagementService
{
	private static final Logger LOG = LoggerFactory.getLogger(DefaultTelefonicaProductEngagementService.class);

	@Override
	public Double getAverageRating(final List<CustomerReviewModel> reviews)
	{
		if (reviews.isEmpty())
		{
			return 0.0;
		}

		return reviews.stream()
			.filter(r -> r.getRating() != null)
			.mapToDouble(CustomerReviewModel::getRating)
			.average()
			.orElse(0.0);
	}

	@Override
	public Map<Integer, Long> getRatingDistribution(final List<CustomerReviewModel> reviews)
	{
		final Map<Integer, Long> distribution = new HashMap<>();
		for (int i = 1; i <= 5; i++)
		{
			distribution.put(i, 0L);
		}

		if (reviews.isEmpty())
		{
			return distribution;
		}

		distribution.putAll(reviews.stream()
			.filter(r -> r.getRating() != null)
			.collect(Collectors.groupingBy(r -> r.getRating().intValue(), Collectors.counting())));
		return distribution;
	}

	@Override
	public Double getVerifiedPurchasePercentage(final List<CustomerReviewModel> reviews)
	{
		if (reviews.isEmpty())
		{
			return 0.0;
		}

		final long verifiedCount = reviews.stream().filter(r -> Boolean.TRUE.equals(r.getVerifiedPurchase())).count();

		return (verifiedCount / (double) reviews.size()) * 100;
	}

	@Override
	public List<CustomerReviewModel> getTopHelpfulReviews(final List<CustomerReviewModel> reviews, final int topN)
	{
		if (reviews.isEmpty())
		{
			return Collections.emptyList();
		}

		return reviews.stream()
			.filter(r -> r.getUsefulCount() != null)
			.sorted((r1, r2) -> Integer.compare(r2.getUsefulCount(), r1.getUsefulCount()))
			.limit(topN)
			.toList();
	}

	@Override
	public Integer getTotalApprovedQuestions(final List<ProductQuestionModel> approvedQuestions)
	{
		return approvedQuestions.size();
	}

	@Override
	public Double getQuestionPublicationRate(final List<ProductQuestionModel> approvedQuestions,
		final List<ProductQuestionModel> allQuestions)
	{
		if (allQuestions.isEmpty())
		{
			return 0.0;
		}

		return (approvedQuestions.size() / (double) allQuestions.size()) * 100;
	}

	@Override
	public Double getAverageAnswerTimeHours(final List<ProductQuestionModel> answeredQuestions)
	{
		if (answeredQuestions.isEmpty())
		{
			return 0.0;
		}

		final double totalHours = answeredQuestions.stream()
			.filter(q -> q.getCreationtime() != null && q.getAnswerModeratedAt() != null)
			.mapToDouble(q -> calculateHoursDifference(q.getCreationtime(), q.getAnswerModeratedAt()))
			.sum();

		return totalHours / answeredQuestions.size();
	}

	private double calculateHoursDifference(final Date start, final Date end)
	{
		if (start == null || end == null)
		{
			return 0.0;
		}

		final long diffMillis = end.getTime() - start.getTime();

		if (diffMillis < 0)
		{
			LOG.warn("answeredAt is before creationtime — returning 0.0");
			return 0.0;
		}

		return TimeUnit.MILLISECONDS.toMinutes(diffMillis) / 60.0;
	}
}
