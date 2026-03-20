package br.com.telefonica.controllers;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.webservicescommons.dto.error.ErrorListWsDTO;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
import de.hybris.platform.webservicescommons.mapping.DataMapper;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.MessageSource;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class BaseControllerTest {

	private static class TestableBaseController extends BaseController {
	}

	private static class RejectingValidator implements Validator {
		@Override
		public boolean supports(final Class<?> clazz) { return true; }
		@Override
		public void validate(final Object target, final Errors errors) {
			errors.reject("review.vote.reviewid.empty");
		}
	}

	private static class AcceptingValidator implements Validator {
		@Override
		public boolean supports(final Class<?> clazz) { return true; }
		@Override
		public void validate(final Object target, final Errors errors) { }
	}

	private TestableBaseController controller;
	private DataMapper dataMapper;
	private I18NService i18nService;
	private MessageSource messageSource;

	@Before
	public void setUp() {
		controller    = new TestableBaseController();
		dataMapper    = mock(DataMapper.class);
		i18nService   = mock(I18NService.class);
		messageSource = mock(MessageSource.class);

		controller.setDataMapper(dataMapper);
		controller.setI18nService(i18nService);
		controller.setMessageSource(messageSource);
	}

	@Test
	public void handleModelNotFoundException_ShouldReturnErrorListWsDTO_WhenExceptionIsReceived() {
		ModelNotFoundException ex = new ModelNotFoundException("Item not found");

		ErrorListWsDTO result = controller.handleModelNotFoundException(ex);

		Assert.assertNotNull(result);
		Assert.assertFalse(result.getErrors().isEmpty());
	}

	@Test
	public void handleModelNotFoundException_ShouldReturnErrorWithCorrectType_WhenExceptionIsReceived() {
		ModelNotFoundException ex = new ModelNotFoundException("Item not found");

		ErrorListWsDTO result = controller.handleModelNotFoundException(ex);

		Assert.assertEquals("UnknownIdentifierError", result.getErrors().get(0).getType());
	}

	@Test
	public void handleModelNotFoundException_ShouldReturnErrorWithSanitizedMessage_WhenExceptionIsReceived() {
		ModelNotFoundException ex = new ModelNotFoundException("Item not found");

		ErrorListWsDTO result = controller.handleModelNotFoundException(ex);

		Assert.assertNotNull(result.getErrors().get(0).getMessage());
	}

	@Test
	public void handleErrorInternal_ShouldReplaceExceptionWithError_WhenTypeContainsException() {
		ErrorListWsDTO result = controller.handleErrorInternal("SomeException", "error message");

		Assert.assertEquals("SomeError", result.getErrors().get(0).getType());
	}

	@Test
	public void handleErrorInternal_ShouldReturnSanitizedMessage_WhenMessageIsProvided() {
		ErrorListWsDTO result = controller.handleErrorInternal("SomeException", "test message");

		Assert.assertEquals("test message", result.getErrors().get(0).getMessage());
	}

	@Test
	public void handleErrorInternal_ShouldReturnSingleError_WhenCalled() {
		ErrorListWsDTO result = controller.handleErrorInternal("SomeException", "test message");

		Assert.assertEquals(1, result.getErrors().size());
	}

	@Test
	public void handleErrorInternal_ShouldHandleNullMessage_WhenMessageIsNull() {
		ErrorListWsDTO result = controller.handleErrorInternal("SomeException", null);

		Assert.assertNotNull(result);
		Assert.assertFalse(result.getErrors().isEmpty());
	}

	@Test
	public void validate_ShouldNotThrow_WhenValidatorPassesWithNoErrors() {
		Validator validator = mock(Validator.class);

		controller.validate("validReviewId", "reviewId", validator);

		verify(validator).validate(anyString(), any(Errors.class));
	}

	@Test
	public void validate_ShouldThrowWebserviceValidationException_WhenValidatorRejectsValue() {
		try {
			controller.validate("", "reviewId", new RejectingValidator());
			Assert.fail("Expected WebserviceValidationException");
		} catch (WebserviceValidationException e) {
			Assert.assertNotNull(e);
		}
	}

	@Test
	public void validate_ShouldNotThrowWebserviceValidationException_WhenValidatorAcceptsValue() {
		try {
			controller.validate("validId", "reviewId", new AcceptingValidator());
		} catch (WebserviceValidationException e) {
			Assert.fail("Should not throw WebserviceValidationException");
		}
	}

	@Test
	public void getDataMapper_ShouldReturnDataMapper_WhenInjected() {
		Assert.assertEquals(dataMapper, controller.getDataMapper());
	}

	@Test
	public void setDataMapper_ShouldReplaceDataMapper_WhenNewMapperIsProvided() {
		DataMapper otherMapper = mock(DataMapper.class);

		controller.setDataMapper(otherMapper);

		Assert.assertEquals(otherMapper, controller.getDataMapper());
	}

	@Test
	public void getI18nService_ShouldReturnI18NService_WhenInjected() {
		Assert.assertEquals(i18nService, controller.getI18nService());
	}

	@Test
	public void setI18nService_ShouldReplaceI18NService_WhenNewServiceIsProvided() {
		I18NService otherService = mock(I18NService.class);

		controller.setI18nService(otherService);

		Assert.assertEquals(otherService, controller.getI18nService());
	}

	@Test
	public void getMessageSource_ShouldReturnMessageSource_WhenInjected() {
		Assert.assertEquals(messageSource, controller.getMessageSource());
	}

	@Test
	public void setMessageSource_ShouldReplaceMessageSource_WhenNewSourceIsProvided() {
		MessageSource otherSource = mock(MessageSource.class);

		controller.setMessageSource(otherSource);

		Assert.assertEquals(otherSource, controller.getMessageSource());
	}
}
