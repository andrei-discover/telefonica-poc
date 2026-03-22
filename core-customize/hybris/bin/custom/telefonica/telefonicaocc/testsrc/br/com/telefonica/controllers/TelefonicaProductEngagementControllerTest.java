package br.com.telefonica.controllers;

import br.com.telefonica.facades.engagement.TelefonicaProductEngagementFacade;
import br.com.telefonica.facades.product.engagement.ProductEngagementSummaryData;
import br.com.telefonica.occ.dto.product.engagement.ProductEngagementSummaryWsDTO;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class TelefonicaProductEngagementControllerTest
{
	private static final String PRODUCT_CODE = "iPhone_13_blue_256g";
	private static final String FIELDS = "DEFAULT";
	private static final int TOP_N = 3;

	@Mock
	private TelefonicaProductEngagementFacade telefonicaProductEngagementFacade;

	@Mock
	private DataMapper dataMapper;

	@InjectMocks
	private TelefonicaProductEngagementController telefonicaEngagementSummaryController;

	private ProductEngagementSummaryData summaryData;
	private ProductEngagementSummaryWsDTO summaryWsDTO;

	@Before
	public void setUp()
	{
		summaryData = new ProductEngagementSummaryData();
		summaryData.setAverageRating(3.8);
		summaryData.setRatingDistribution(buildRatingDistribution());
		summaryData.setVerifiedPurchasePercentage(60.0);
		summaryData.setTopHelpfulReviews(List.of());
		summaryData.setTotalQuestions(3);
		summaryData.setQuestionPublicationRate(60.0);
		summaryData.setAverageAnswerTimeHours(48.0);

		summaryWsDTO = new ProductEngagementSummaryWsDTO();
		summaryWsDTO.setAverageRating(3.8);
		summaryWsDTO.setRatingDistribution(buildRatingDistribution());
		summaryWsDTO.setVerifiedPurchasePercentage(60.0);
		summaryWsDTO.setTopHelpfulReviews(List.of());
		summaryWsDTO.setTotalQuestions(3);
		summaryWsDTO.setQuestionPublicationRate(60.0);
		summaryWsDTO.setAverageAnswerTimeHours(48.0);
	}

	@Test
	public void testGetEngagementSummary()
	{
		// Given
		given(telefonicaProductEngagementFacade.getEngagementSummary(eq(PRODUCT_CODE), eq(TOP_N))).willReturn(summaryData);
		given(dataMapper.map(eq(summaryData), eq(ProductEngagementSummaryWsDTO.class), eq(FIELDS))).willReturn(summaryWsDTO);

		// When
		final ProductEngagementSummaryWsDTO result =
			telefonicaEngagementSummaryController.getEngagementSummary(PRODUCT_CODE, TOP_N, FIELDS);

		// Then
		assertNotNull(result);
		assertEquals(Double.valueOf(3.8), result.getAverageRating());
		assertEquals(Integer.valueOf(3), result.getTotalQuestions());
		assertEquals(Double.valueOf(60.0), result.getVerifiedPurchasePercentage());
		assertEquals(Double.valueOf(48.0), result.getAverageAnswerTimeHours());

		verify(telefonicaProductEngagementFacade).getEngagementSummary(PRODUCT_CODE, TOP_N);
		verify(dataMapper).map(summaryData, ProductEngagementSummaryWsDTO.class, FIELDS);
	}

	@Test
	public void testGetEngagementSummaryWithDefaultTopN()
	{
		// Given
		given(telefonicaProductEngagementFacade.getEngagementSummary(eq(PRODUCT_CODE), anyInt())).willReturn(summaryData);

		given(dataMapper.map(any(), eq(ProductEngagementSummaryWsDTO.class), anyString())).willReturn(summaryWsDTO);

		// When
		final ProductEngagementSummaryWsDTO result =
			telefonicaEngagementSummaryController.getEngagementSummary(PRODUCT_CODE, 5, FIELDS);

		// Then
		assertNotNull(result);
		verify(telefonicaProductEngagementFacade).getEngagementSummary(PRODUCT_CODE, 5);
	}

	@Test
	public void testGetEngagementSummaryEmptyProduct()
	{
		// Given
		final ProductEngagementSummaryData emptyData = new ProductEngagementSummaryData();
		emptyData.setAverageRating(0.0);
		emptyData.setRatingDistribution(buildEmptyRatingDistribution());
		emptyData.setVerifiedPurchasePercentage(0.0);
		emptyData.setTopHelpfulReviews(List.of());
		emptyData.setTotalQuestions(0);
		emptyData.setQuestionPublicationRate(0.0);
		emptyData.setAverageAnswerTimeHours(0.0);

		final ProductEngagementSummaryWsDTO emptyWsDTO = new ProductEngagementSummaryWsDTO();
		emptyWsDTO.setAverageRating(0.0);
		emptyWsDTO.setTotalQuestions(0);

		given(telefonicaProductEngagementFacade.getEngagementSummary(eq(PRODUCT_CODE), anyInt())).willReturn(emptyData);

		given(dataMapper.map(any(), eq(ProductEngagementSummaryWsDTO.class), anyString())).willReturn(emptyWsDTO);

		// When
		final ProductEngagementSummaryWsDTO result =
			telefonicaEngagementSummaryController.getEngagementSummary(PRODUCT_CODE, TOP_N, FIELDS);

		// Then
		assertNotNull(result);
		assertEquals(Double.valueOf(0.0), result.getAverageRating());
		assertEquals(Integer.valueOf(0), result.getTotalQuestions());
	}

	private Map<Integer, Long> buildRatingDistribution()
	{
		final Map<Integer, Long> distribution = new HashMap<>();
		distribution.put(1, 0L);
		distribution.put(2, 1L);
		distribution.put(3, 1L);
		distribution.put(4, 1L);
		distribution.put(5, 2L);
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
