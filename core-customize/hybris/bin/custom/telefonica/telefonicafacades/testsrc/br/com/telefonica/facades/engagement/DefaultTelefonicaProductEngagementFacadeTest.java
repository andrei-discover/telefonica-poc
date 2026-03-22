package br.com.telefonica.facades.engagement;

import br.com.telefonica.core.dao.TelefonicaProductEngagementDao;
import br.com.telefonica.core.model.ProductQuestionModel;
import br.com.telefonica.core.service.TelefonicaProductEngagementService;
import br.com.telefonica.facades.engagement.impl.DefaultTelefonicaProductProductEngagementFacade;
import br.com.telefonica.facades.product.engagement.ProductEngagementSummaryData;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.ReviewData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.customerreview.model.CustomerReviewModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultTelefonicaProductEngagementFacadeTest
{
	private static final String PRODUCT_CODE = "iPhone_13_blue_256g";
	private static final int TOP_N = 3;

	@Mock
	private TelefonicaProductEngagementDao telefonicaProductEngagementDao;

	@Mock
	private TelefonicaProductEngagementService telefonicaProductEngagementService;

	@Mock
	private ProductService productService;

	@Mock
	private Converter<CustomerReviewModel, ReviewData> customerReviewConverter;

	@InjectMocks
	private DefaultTelefonicaProductProductEngagementFacade facade;

	private ProductModel product;
	private List<CustomerReviewModel> approvedReviews;
	private List<ProductQuestionModel> approvedQuestions;
	private List<ProductQuestionModel> allQuestions;
	private List<ProductQuestionModel> answeredQuestions;

	@Before
	public void setUp()
	{
		product = new ProductModel();

		final CustomerReviewModel review1 = new CustomerReviewModel();
		review1.setRating(5.0);
		review1.setUsefulCount(10);
		review1.setVerifiedPurchase(Boolean.TRUE);

		final CustomerReviewModel review2 = new CustomerReviewModel();
		review2.setRating(3.0);
		review2.setUsefulCount(5);
		review2.setVerifiedPurchase(Boolean.FALSE);

		approvedReviews = List.of(review1, review2);

		final ProductQuestionModel question1 = new ProductQuestionModel();
		final ProductQuestionModel question2 = new ProductQuestionModel();

		approvedQuestions = List.of(question1, question2);
		allQuestions = List.of(question1, question2, new ProductQuestionModel());
		answeredQuestions = List.of(question1);
	}

	@Test
	public void testGetEngagementSummaryCallsAllDaoMethods()
	{
		// Given
		given(productService.getProductForCode(PRODUCT_CODE)).willReturn(product);
		given(telefonicaProductEngagementDao.findApprovedReviews(product)).willReturn(approvedReviews);
		given(telefonicaProductEngagementDao.findApprovedQuestions(product)).willReturn(approvedQuestions);
		given(telefonicaProductEngagementDao.findAllQuestions(product)).willReturn(allQuestions);
		given(telefonicaProductEngagementDao.findAnsweredQuestions(product)).willReturn(answeredQuestions);

		given(telefonicaProductEngagementService.getAverageRating(approvedReviews)).willReturn(4.0);
		given(telefonicaProductEngagementService.getRatingDistribution(approvedReviews)).willReturn(buildRatingDistribution());
		given(telefonicaProductEngagementService.getVerifiedPurchasePercentage(approvedReviews)).willReturn(50.0);
		given(telefonicaProductEngagementService.getTopHelpfulReviews(approvedReviews, TOP_N)).willReturn(approvedReviews);
		given(telefonicaProductEngagementService.getTotalApprovedQuestions(approvedQuestions)).willReturn(2);
		given(telefonicaProductEngagementService.getQuestionPublicationRate(approvedQuestions, allQuestions)).willReturn(66.6);
		given(telefonicaProductEngagementService.getAverageAnswerTimeHours(answeredQuestions)).willReturn(48.0);

		// When
		final ProductEngagementSummaryData result = facade.getEngagementSummary(PRODUCT_CODE, TOP_N);

		// Then
		assertNotNull(result);
		assertEquals(Double.valueOf(4.0), result.getAverageRating());
		assertEquals(Double.valueOf(50.0), result.getVerifiedPurchasePercentage());
		assertEquals(Integer.valueOf(2), result.getTotalQuestions());
		assertEquals(Double.valueOf(66.6), result.getQuestionPublicationRate());
		assertEquals(Double.valueOf(48.0), result.getAverageAnswerTimeHours());

		verify(telefonicaProductEngagementDao).findApprovedReviews(product);
		verify(telefonicaProductEngagementDao).findApprovedQuestions(product);
		verify(telefonicaProductEngagementDao).findAllQuestions(product);
		verify(telefonicaProductEngagementDao).findAnsweredQuestions(product);

		verify(telefonicaProductEngagementService).getAverageRating(approvedReviews);
		verify(telefonicaProductEngagementService).getRatingDistribution(approvedReviews);
		verify(telefonicaProductEngagementService).getVerifiedPurchasePercentage(approvedReviews);
		verify(telefonicaProductEngagementService).getTopHelpfulReviews(approvedReviews, TOP_N);
		verify(telefonicaProductEngagementService).getTotalApprovedQuestions(approvedQuestions);
		verify(telefonicaProductEngagementService).getQuestionPublicationRate(approvedQuestions, allQuestions);
		verify(telefonicaProductEngagementService).getAverageAnswerTimeHours(answeredQuestions);
	}

	@Test
	public void testGetEngagementSummaryWithDefaultTopN()
	{
		// Given
		given(productService.getProductForCode(PRODUCT_CODE)).willReturn(product);
		given(telefonicaProductEngagementDao.findApprovedReviews(product)).willReturn(approvedReviews);
		given(telefonicaProductEngagementDao.findApprovedQuestions(product)).willReturn(approvedQuestions);
		given(telefonicaProductEngagementDao.findAllQuestions(product)).willReturn(allQuestions);
		given(telefonicaProductEngagementDao.findAnsweredQuestions(product)).willReturn(answeredQuestions);

		given(telefonicaProductEngagementService.getAverageRating(anyList())).willReturn(4.0);
		given(telefonicaProductEngagementService.getRatingDistribution(anyList())).willReturn(buildRatingDistribution());
		given(telefonicaProductEngagementService.getVerifiedPurchasePercentage(anyList())).willReturn(50.0);
		given(telefonicaProductEngagementService.getTopHelpfulReviews(anyList(), anyInt())).willReturn(approvedReviews);
		given(telefonicaProductEngagementService.getTotalApprovedQuestions(anyList())).willReturn(2);
		given(telefonicaProductEngagementService.getQuestionPublicationRate(anyList(), anyList())).willReturn(66.6);
		given(telefonicaProductEngagementService.getAverageAnswerTimeHours(anyList())).willReturn(48.0);

		// When — topN = 0 deve usar DEFAULT_TOP_N = 5
		final ProductEngagementSummaryData result = facade.getEngagementSummary(PRODUCT_CODE, 0);

		// Then
		assertNotNull(result);
		verify(telefonicaProductEngagementService).getTopHelpfulReviews(approvedReviews, 5);
	}

	@Test
	public void testGetEngagementSummaryEmptyProduct()
	{
		// Given
		given(productService.getProductForCode(PRODUCT_CODE)).willReturn(product);
		given(telefonicaProductEngagementDao.findApprovedReviews(product)).willReturn(Collections.emptyList());
		given(telefonicaProductEngagementDao.findApprovedQuestions(product)).willReturn(Collections.emptyList());
		given(telefonicaProductEngagementDao.findAllQuestions(product)).willReturn(Collections.emptyList());
		given(telefonicaProductEngagementDao.findAnsweredQuestions(product)).willReturn(Collections.emptyList());

		given(telefonicaProductEngagementService.getAverageRating(anyList())).willReturn(0.0);
		given(telefonicaProductEngagementService.getRatingDistribution(anyList())).willReturn(buildEmptyRatingDistribution());
		given(telefonicaProductEngagementService.getVerifiedPurchasePercentage(anyList())).willReturn(0.0);
		given(telefonicaProductEngagementService.getTopHelpfulReviews(anyList(), anyInt())).willReturn(Collections.emptyList());
		given(telefonicaProductEngagementService.getTotalApprovedQuestions(anyList())).willReturn(0);
		given(telefonicaProductEngagementService.getQuestionPublicationRate(anyList(), anyList())).willReturn(0.0);
		given(telefonicaProductEngagementService.getAverageAnswerTimeHours(anyList())).willReturn(0.0);

		// When
		final ProductEngagementSummaryData result = facade.getEngagementSummary(PRODUCT_CODE, TOP_N);

		// Then
		assertNotNull(result);
		assertEquals(Double.valueOf(0.0), result.getAverageRating());
		assertEquals(Integer.valueOf(0), result.getTotalQuestions());
		assertEquals(Double.valueOf(0.0), result.getQuestionPublicationRate());
		assertEquals(Double.valueOf(0.0), result.getAverageAnswerTimeHours());
	}

	private Map<Integer, Long> buildRatingDistribution()
	{
		final Map<Integer, Long> distribution = new HashMap<>();
		distribution.put(1, 0L);
		distribution.put(2, 1L);
		distribution.put(3, 0L);
		distribution.put(4, 0L);
		distribution.put(5, 1L);
		return distribution;
	}

	private Map<Integer, Long> buildEmptyRatingDistribution()
	{
		final Map<Integer, Long> distribution = new HashMap<>();
		distribution.put(1, 0L);
		distribution.put(2, 0L);
		distribution.put(3, 0L);
		distribution.put(4, 0L);
		distribution.put(5, 0L);
		return distribution;
	}
}
