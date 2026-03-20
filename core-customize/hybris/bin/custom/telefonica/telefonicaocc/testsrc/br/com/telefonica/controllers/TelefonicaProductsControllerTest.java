package br.com.telefonica.controllers;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.product.data.ReviewData;
import de.hybris.platform.commercewebservicescommons.dto.product.ReviewWsDTO;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
import de.hybris.platform.webservicescommons.mapping.DataMapper;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.lang.reflect.Field;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class TelefonicaProductsControllerTest {

	private static class AcceptingValidator implements Validator {
		@Override
		public boolean supports(final Class<?> clazz) { return true; }
		@Override
		public void validate(final Object target, final Errors errors) { }
	}

	private static class RejectingValidator implements Validator {
		@Override
		public boolean supports(final Class<?> clazz) { return true; }
		@Override
		public void validate(final Object target, final Errors errors) {
			errors.reject("review.invalid");
		}
	}

	@InjectMocks
	private TelefonicaProductsController controller;

	@Mock
	private ProductFacade productFacade;

	@Mock
	private DataMapper dataMapper;

	private static final String PRODUCT_CODE = "iPhone_13_blue_128g";
	private static final String FIELDS       = "DEFAULT";

	@Before
	public void setUp() throws Exception {
		injectValidator(new AcceptingValidator());
		injectDataMapper(dataMapper);
	}

	private void injectValidator(final Validator validator) throws Exception {
		final Field field =
			TelefonicaProductsController.class.getDeclaredField("reviewDTOValidator");
		field.setAccessible(true);
		field.set(controller, validator);
	}

	private void injectDataMapper(final DataMapper mapper) throws Exception {
		final Field field = BaseController.class.getDeclaredField("dataMapper");
		field.setAccessible(true);
		field.set(controller, mapper);
	}

	@Test
	public void createProductReview_ShouldReturnReviewWsDTO_WhenReviewIsValid() {
		final ReviewWsDTO review        = new ReviewWsDTO();
		final ReviewData reviewData     = new ReviewData();
		final ReviewData reviewDataRet  = new ReviewData();
		final ReviewWsDTO expectedWsDTO = new ReviewWsDTO();

		when(dataMapper.map(review, ReviewData.class, "alias,rating,headline,comment"))
			.thenReturn(reviewData);
		when(productFacade.postReview(PRODUCT_CODE, reviewData)).thenReturn(reviewDataRet);
		when(dataMapper.map(reviewDataRet, ReviewWsDTO.class, FIELDS)).thenReturn(expectedWsDTO);

		final ReviewWsDTO result =
			controller.createProductReview(PRODUCT_CODE, review, FIELDS);

		Assert.assertNotNull(result);
		Assert.assertEquals(expectedWsDTO, result);
	}

	@Test
	public void createProductReview_ShouldDelegateToProductFacade_WhenReviewIsValid() {
		final ReviewWsDTO review       = new ReviewWsDTO();
		final ReviewData reviewData    = new ReviewData();
		final ReviewData reviewDataRet = new ReviewData();

		when(dataMapper.map(review, ReviewData.class, "alias,rating,headline,comment"))
			.thenReturn(reviewData);
		when(productFacade.postReview(PRODUCT_CODE, reviewData)).thenReturn(reviewDataRet);
		when(dataMapper.map(reviewDataRet, ReviewWsDTO.class, FIELDS))
			.thenReturn(new ReviewWsDTO());

		controller.createProductReview(PRODUCT_CODE, review, FIELDS);

		verify(productFacade).postReview(PRODUCT_CODE, reviewData);
	}

	@Test
	public void createProductReview_ShouldMapReviewToReviewData_WhenCalled() {
		final ReviewWsDTO review       = new ReviewWsDTO();
		final ReviewData reviewData    = new ReviewData();
		final ReviewData reviewDataRet = new ReviewData();

		when(dataMapper.map(review, ReviewData.class, "alias,rating,headline,comment"))
			.thenReturn(reviewData);
		when(productFacade.postReview(PRODUCT_CODE, reviewData)).thenReturn(reviewDataRet);
		when(dataMapper.map(reviewDataRet, ReviewWsDTO.class, FIELDS))
			.thenReturn(new ReviewWsDTO());

		controller.createProductReview(PRODUCT_CODE, review, FIELDS);

		verify(dataMapper).map(review, ReviewData.class, "alias,rating,headline,comment");
	}

	@Test
	public void createProductReview_ShouldMapReturnedDataToWsDTO_WhenCalled() {
		final ReviewWsDTO review       = new ReviewWsDTO();
		final ReviewData reviewData    = new ReviewData();
		final ReviewData reviewDataRet = new ReviewData();

		when(dataMapper.map(review, ReviewData.class, "alias,rating,headline,comment"))
			.thenReturn(reviewData);
		when(productFacade.postReview(PRODUCT_CODE, reviewData)).thenReturn(reviewDataRet);
		when(dataMapper.map(reviewDataRet, ReviewWsDTO.class, FIELDS))
			.thenReturn(new ReviewWsDTO());

		controller.createProductReview(PRODUCT_CODE, review, FIELDS);

		verify(dataMapper).map(reviewDataRet, ReviewWsDTO.class, FIELDS);
	}

	@Test
	public void createProductReview_ShouldThrowWebserviceValidationException_WhenReviewIsInvalid()
		throws Exception {
		injectValidator(new RejectingValidator());
		final ReviewWsDTO review = new ReviewWsDTO();

		try {
			controller.createProductReview(PRODUCT_CODE, review, FIELDS);
			Assert.fail("Expected WebserviceValidationException");
		} catch (WebserviceValidationException e) {
			Assert.assertNotNull(e);
		}

		verify(productFacade, never()).postReview(anyString(), any());
	}

	@Test
	public void createProductReview_ShouldNotCallFacade_WhenValidationFails() throws Exception {
		injectValidator(new RejectingValidator());
		final ReviewWsDTO review = new ReviewWsDTO();

		try {
			controller.createProductReview(PRODUCT_CODE, review, FIELDS);
		} catch (WebserviceValidationException e) {
		}

		verify(productFacade, never()).postReview(anyString(), any());
	}
}
