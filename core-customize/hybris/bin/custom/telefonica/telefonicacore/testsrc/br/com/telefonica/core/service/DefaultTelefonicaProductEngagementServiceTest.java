package br.com.telefonica.core.service;

import br.com.telefonica.core.model.ProductQuestionModel;
import br.com.telefonica.core.service.impl.DefaultTelefonicaProductEngagementService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.customerreview.model.CustomerReviewModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultTelefonicaProductEngagementServiceTest
{
	@InjectMocks
	private DefaultTelefonicaProductEngagementService service;

	private CustomerReviewModel review1;
	private CustomerReviewModel review2;
	private CustomerReviewModel review3;

	private ProductQuestionModel question1;
	private ProductQuestionModel question2;
	private ProductQuestionModel question3;

	@Before
	public void setUp()
	{
		// Reviews
		review1 = new CustomerReviewModel();
		review1.setRating(5.0);
		review1.setUsefulCount(10);
		review1.setVerifiedPurchase(Boolean.TRUE);

		review2 = new CustomerReviewModel();
		review2.setRating(3.0);
		review2.setUsefulCount(5);
		review2.setVerifiedPurchase(Boolean.TRUE);

		review3 = new CustomerReviewModel();
		review3.setRating(1.0);
		review3.setUsefulCount(2);
		review3.setVerifiedPurchase(Boolean.FALSE);

		// Questions
		question1 = new ProductQuestionModel();
		question1.setCreationtime(new Date(0));
		question1.setAnswerModeratedAt(new Date(3600000)); // +1h

		question2 = new ProductQuestionModel();
		question2.setCreationtime(new Date(0));
		question2.setAnswerModeratedAt(new Date(7200000)); // +2h

		question3 = new ProductQuestionModel();
	}

	@Test
	public void testGetAverageRatingWithReviews()
	{
		// Given
		final List<CustomerReviewModel> reviews = List.of(review1, review2, review3);

		// When
		final Double result = service.getAverageRating(reviews);

		// Then
		assertEquals(Double.valueOf(3.0), result);
	}

	@Test
	public void testGetAverageRatingEmptyList()
	{
		// When
		final Double result = service.getAverageRating(Collections.emptyList());

		// Then
		assertEquals(Double.valueOf(0.0), result);
	}

	@Test
	public void testGetAverageRatingWithNullRating()
	{
		// Given
		final CustomerReviewModel reviewWithNullRating = new CustomerReviewModel();
		reviewWithNullRating.setRating(null);
		final List<CustomerReviewModel> reviews = List.of(review1, reviewWithNullRating);

		// When
		final Double result = service.getAverageRating(reviews);

		// Then
		assertEquals(Double.valueOf(5.0), result);
	}

	@Test
	public void testGetRatingDistribution()
	{
		// Given
		final List<CustomerReviewModel> reviews = List.of(review1, review2, review3);

		// When
		final Map<Integer, Long> result = service.getRatingDistribution(reviews);

		// Then
		assertNotNull(result);
		assertEquals(5, result.size());
		assertEquals(Long.valueOf(1), result.get(1));
		assertEquals(Long.valueOf(0), result.get(2));
		assertEquals(Long.valueOf(1), result.get(3));
		assertEquals(Long.valueOf(0), result.get(4));
		assertEquals(Long.valueOf(1), result.get(5));
	}

	@Test
	public void testGetRatingDistributionEmptyList()
	{
		// When
		final Map<Integer, Long> result = service.getRatingDistribution(Collections.emptyList());

		// Then
		assertNotNull(result);
		assertEquals(5, result.size());
		for (int i = 1; i <= 5; i++)
		{
			assertEquals(Long.valueOf(0), result.get(i));
		}
	}

	@Test
	public void testGetRatingDistributionAllSameRating()
	{
		// Given
		final CustomerReviewModel review5a = new CustomerReviewModel();
		review5a.setRating(5.0);
		final CustomerReviewModel review5b = new CustomerReviewModel();
		review5b.setRating(5.0);
		final List<CustomerReviewModel> reviews = List.of(review5a, review5b);

		// When
		final Map<Integer, Long> result = service.getRatingDistribution(reviews);

		// Then
		assertEquals(Long.valueOf(2), result.get(5));
		assertEquals(Long.valueOf(0), result.get(1));
		assertEquals(Long.valueOf(0), result.get(2));
		assertEquals(Long.valueOf(0), result.get(3));
		assertEquals(Long.valueOf(0), result.get(4));
	}

	@Test
	public void testGetVerifiedPurchasePercentage()
	{
		// Given — 2 de 3 são verified
		final List<CustomerReviewModel> reviews = List.of(review1, review2, review3);

		// When
		final Double result = service.getVerifiedPurchasePercentage(reviews);

		// Then
		assertEquals(Double.valueOf(66.66666666666666), result);
	}

	@Test
	public void testGetVerifiedPurchasePercentageAllVerified()
	{
		// Given
		final List<CustomerReviewModel> reviews = List.of(review1, review2);

		// When
		final Double result = service.getVerifiedPurchasePercentage(reviews);

		// Then
		assertEquals(Double.valueOf(100.0), result);
	}

	@Test
	public void testGetVerifiedPurchasePercentageNoneVerified()
	{
		// Given
		final List<CustomerReviewModel> reviews = List.of(review3);

		// When
		final Double result = service.getVerifiedPurchasePercentage(reviews);

		// Then
		assertEquals(Double.valueOf(0.0), result);
	}

	@Test
	public void testGetVerifiedPurchasePercentageEmptyList()
	{
		// When
		final Double result = service.getVerifiedPurchasePercentage(Collections.emptyList());

		// Then
		assertEquals(Double.valueOf(0.0), result);
	}

	@Test
	public void testGetTopHelpfulReviews()
	{
		// Given
		final List<CustomerReviewModel> reviews = List.of(review1, review2, review3);

		// When
		final List<CustomerReviewModel> result = service.getTopHelpfulReviews(reviews, 2);

		// Then
		assertNotNull(result);
		assertEquals(2, result.size());
		assertEquals(Integer.valueOf(10), result.get(0).getUsefulCount());
		assertEquals(Integer.valueOf(5), result.get(1).getUsefulCount());
	}

	@Test
	public void testGetTopHelpfulReviewsTopNGreaterThanList()
	{
		// Given
		final List<CustomerReviewModel> reviews = List.of(review1, review2);

		// When
		final List<CustomerReviewModel> result = service.getTopHelpfulReviews(reviews, 10);

		// Then
		assertEquals(2, result.size());
	}

	@Test
	public void testGetTopHelpfulReviewsEmptyList()
	{
		// When
		final List<CustomerReviewModel> result = service.getTopHelpfulReviews(Collections.emptyList(), 3);

		// Then
		assertTrue(result.isEmpty());
	}

	@Test
	public void testGetTopHelpfulReviewsWithNullVotes()
	{
		// Given
		final CustomerReviewModel reviewWithNullVotes = new CustomerReviewModel();
		reviewWithNullVotes.getUsefulCount();
		final List<CustomerReviewModel> reviews = List.of(review1, reviewWithNullVotes);

		// When
		final List<CustomerReviewModel> result = service.getTopHelpfulReviews(reviews, 3);

		// Then
		assertEquals(1, result.size());
		assertEquals(Integer.valueOf(10), result.get(0).getUsefulCount());
	}

	@Test
	public void testGetTotalApprovedQuestions()
	{
		// Given
		final List<ProductQuestionModel> questions = List.of(question1, question2);

		// When
		final Integer result = service.getTotalApprovedQuestions(questions);

		// Then
		assertEquals(Integer.valueOf(2), result);
	}

	@Test
	public void testGetTotalApprovedQuestionsEmptyList()
	{
		// When
		final Integer result = service.getTotalApprovedQuestions(Collections.emptyList());

		// Then
		assertEquals(Integer.valueOf(0), result);
	}

	@Test
	public void testGetQuestionPublicationRate()
	{
		// Given
		final List<ProductQuestionModel> approved = List.of(question1, question2);
		final List<ProductQuestionModel> all = List.of(question1, question2, question3);

		// When
		final Double result = service.getQuestionPublicationRate(approved, all);

		// Then
		assertEquals(Double.valueOf(66.66666666666666), result);
	}

	@Test
	public void testGetQuestionPublicationRateAllApproved()
	{
		// Given
		final List<ProductQuestionModel> approved = List.of(question1, question2);
		final List<ProductQuestionModel> all = List.of(question1, question2);

		// When
		final Double result = service.getQuestionPublicationRate(approved, all);

		// Then
		assertEquals(Double.valueOf(100.0), result);
	}

	@Test
	public void testGetQuestionPublicationRateNoneApproved()
	{
		// Given
		final List<ProductQuestionModel> approved = Collections.emptyList();
		final List<ProductQuestionModel> all = List.of(question1, question2);

		// When
		final Double result = service.getQuestionPublicationRate(approved, all);

		// Then
		assertEquals(Double.valueOf(0.0), result);
	}

	@Test
	public void testGetQuestionPublicationRateEmptyAll()
	{
		// When
		final Double result = service.getQuestionPublicationRate(Collections.emptyList(), Collections.emptyList());

		// Then
		assertEquals(Double.valueOf(0.0), result);
	}

	@Test
	public void testGetAverageAnswerTimeHours()
	{
		// Given — question1(1h), question2(2h) → média 1.5h
		final List<ProductQuestionModel> questions = List.of(question1, question2);

		// When
		final Double result = service.getAverageAnswerTimeHours(questions);

		// Then
		assertEquals(Double.valueOf(1.5), result);
	}

	@Test
	public void testGetAverageAnswerTimeHoursEmptyList()
	{
		// When
		final Double result = service.getAverageAnswerTimeHours(Collections.emptyList());

		// Then
		assertEquals(Double.valueOf(0.0), result);
	}

	@Test
	public void testGetAverageAnswerTimeHoursNullAnsweredAt()
	{
		// Given
		final List<ProductQuestionModel> questions = List.of(question3);

		// When
		final Double result = service.getAverageAnswerTimeHours(questions);

		// Then
		assertEquals(Double.valueOf(0.0), result);
	}

	@Test
	public void testGetAverageAnswerTimeHoursNegativeDiff()
	{
		// Given
		final ProductQuestionModel questionWithNegativeDiff = new ProductQuestionModel();
		questionWithNegativeDiff.setCreationtime(new Date(7200000));
		questionWithNegativeDiff.setAnswerModeratedAt(new Date(0));

		final List<ProductQuestionModel> questions = List.of(questionWithNegativeDiff);

		// When
		final Double result = service.getAverageAnswerTimeHours(questions);

		// Then
		assertEquals(Double.valueOf(0.0), result);
	}
}
