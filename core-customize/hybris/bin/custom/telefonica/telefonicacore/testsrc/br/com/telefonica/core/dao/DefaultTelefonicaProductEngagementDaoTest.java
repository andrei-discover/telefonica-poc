package br.com.telefonica.core.dao;

import static org.assertj.core.api.Assertions.assertThat;

import br.com.telefonica.core.model.ProductQuestionModel;
import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.customerreview.model.CustomerReviewModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.ServicelayerTest;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class DefaultTelefonicaProductEngagementDaoTest extends ServicelayerTest
{
	@Resource
	private TelefonicaProductEngagementDao telefonicaEngagementSummaryDao;

	@Resource
	private ProductService productService;

	@Resource
	private CatalogVersionService catalogVersionService;

	private ProductModel testProduct;

	@Before
	public void setUp() throws ImpExException
	{
		ServicelayerTest.importCsv("/telefonicacore/test/telefonica-product-engagement-dao-test.impex", "utf-8");

		final CatalogVersionModel catalogVersion = catalogVersionService.getCatalogVersion("testCatalog", "Online");

		testProduct = productService.getProductForCode(catalogVersion, "testProduct");
	}

	@Test
	public void testFindApprovedReviewsReturnsOnlyApproved()
	{
		// When
		final List<CustomerReviewModel> result = telefonicaEngagementSummaryDao.findApprovedReviews(testProduct);

		// Then —
		assertThat(result).hasSize(2);
		assertThat(result).extracting(CustomerReviewModel::getHeadline)
			.containsExactlyInAnyOrder("Great product!", "Good product!");
	}

	@Test
	public void testFindApprovedReviewsDoesNotReturnPending()
	{
		// When
		final List<CustomerReviewModel> result = telefonicaEngagementSummaryDao.findApprovedReviews(testProduct);

		// Then
		assertThat(result).extracting(CustomerReviewModel::getHeadline).doesNotContain("Average product!");
	}

	@Test
	public void testFindApprovedReviewsReturnsEmptyForUnknownProduct()
	{
		// Given
		final ProductModel unknownProduct = new ProductModel();
		unknownProduct.setCode("unknownProduct");

		// When
		final List<CustomerReviewModel> result = telefonicaEngagementSummaryDao.findApprovedReviews(unknownProduct);

		// Then
		assertThat(result).isEmpty();
	}

	@Test
	public void testFindApprovedQuestionsReturnsOnlyApproved()
	{
		// When
		final List<ProductQuestionModel> result = telefonicaEngagementSummaryDao.findApprovedQuestions(testProduct);

		// Then — deve retornar apenas as 2 perguntas APPROVED
		assertThat(result).hasSize(2);
		assertThat(result).extracting(ProductQuestionModel::getQuestion)
			.containsExactlyInAnyOrder("Does it have warranty?", "What is the battery capacity?");
	}

	@Test
	public void testFindApprovedQuestionsDoesNotReturnPending()
	{
		// When
		final List<ProductQuestionModel> result = telefonicaEngagementSummaryDao.findApprovedQuestions(testProduct);

		// Then
		assertThat(result).extracting(ProductQuestionModel::getQuestion).doesNotContain("Is it 5G compatible?");
	}

	@Test
	public void testFindAllQuestionsReturnsAll()
	{
		// When
		final List<ProductQuestionModel> result = telefonicaEngagementSummaryDao.findAllQuestions(testProduct);

		// Then
		assertThat(result).hasSize(3);
	}

	@Test
	public void testFindAllQuestionsContainsPendingAndApproved()
	{
		// When
		final List<ProductQuestionModel> result = telefonicaEngagementSummaryDao.findAllQuestions(testProduct);

		// Then
		assertThat(result).extracting(ProductQuestionModel::getQuestion)
			.containsExactlyInAnyOrder("Does it have warranty?", "What is the battery capacity?", "Is it 5G compatible?");
	}

	@Test
	public void testFindAllQuestionsReturnsEmptyForUnknownProduct()
	{
		// Given
		final ProductModel unknownProduct = new ProductModel();
		unknownProduct.setCode("unknownProduct");

		// When
		final List<ProductQuestionModel> result = telefonicaEngagementSummaryDao.findAllQuestions(unknownProduct);

		// Then
		assertThat(result).isEmpty();
	}

	@Test
	public void testFindAnsweredQuestionsReturnsOnlyAnswered()
	{
		// When
		final List<ProductQuestionModel> result = telefonicaEngagementSummaryDao.findAnsweredQuestions(testProduct);

		// Then
		assertThat(result).hasSize(2);
		assertThat(result).extracting(ProductQuestionModel::getQuestion)
			.containsExactlyInAnyOrder("Does it have warranty?", "What is the battery capacity?");
	}

	@Test
	public void testFindAnsweredQuestionsDoesNotReturnPending()
	{
		// When
		final List<ProductQuestionModel> result = telefonicaEngagementSummaryDao.findAnsweredQuestions(testProduct);

		// Then
		assertThat(result).extracting(ProductQuestionModel::getQuestion).doesNotContain("Is it 5G compatible?");
	}

	@Test
	public void testFindAnsweredQuestionsReturnsEmptyForUnknownProduct()
	{
		// Given
		final ProductModel unknownProduct = new ProductModel();
		unknownProduct.setCode("unknownProduct");

		// When
		final List<ProductQuestionModel> result = telefonicaEngagementSummaryDao.findAnsweredQuestions(unknownProduct);

		// Then
		assertThat(result).isEmpty();
	}
}
