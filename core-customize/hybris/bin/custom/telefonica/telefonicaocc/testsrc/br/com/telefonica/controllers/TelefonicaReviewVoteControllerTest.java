package br.com.telefonica.controllers;

import br.com.telefonica.facades.review.TelefonicaReviewVoteFacade;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;

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

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class TelefonicaReviewVoteControllerTest {

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

	@InjectMocks
	private TelefonicaReviewVoteController controller;

	@Mock
	private TelefonicaReviewVoteFacade telefonicaReviewVoteFacade;

	@Mock
	private Validator telefonicaReviewVoteValidator;

	private static final String REVIEW_ID = "123456789";

	@Before
	public void setUp() throws Exception {
		injectValidator(new AcceptingValidator());
	}

	private void injectValidator(final Validator validator) throws Exception {
		final Field field =
			TelefonicaReviewVoteController.class.getDeclaredField("telefonicaReviewVoteValidator");
		field.setAccessible(true);
		field.set(controller, validator);
	}

	@Test
	public void voteHelpful_ShouldDelegateToFacade_WhenReviewIdIsValid() {
		controller.voteHelpful(REVIEW_ID);

		verify(telefonicaReviewVoteFacade).voteHelpful(REVIEW_ID);
	}

	@Test
	public void voteHelpful_ShouldThrowWebserviceValidationException_WhenReviewIdIsBlank()
		throws Exception {
		injectValidator(new RejectingValidator());

		try {
			controller.voteHelpful("");
			Assert.fail("Expected WebserviceValidationException");
		} catch (WebserviceValidationException e) {
			Assert.assertNotNull(e);
		}

		verify(telefonicaReviewVoteFacade, never()).voteHelpful(anyString());
	}

	@Test
	public void voteHelpful_ShouldNotCallFacade_WhenValidationFails() throws Exception {
		injectValidator(new RejectingValidator());

		try {
			controller.voteHelpful("");
		} catch (WebserviceValidationException e) {
		}

		verify(telefonicaReviewVoteFacade, never()).voteHelpful(anyString());
	}

	@Test
	public void voteHelpful_ShouldThrowIllegalStateException_WhenFacadeThrowsDuplicateVote() {
		doThrow(new IllegalStateException("User already voted"))
			.when(telefonicaReviewVoteFacade).voteHelpful(REVIEW_ID);

		try {
			controller.voteHelpful(REVIEW_ID);
			Assert.fail("Expected IllegalStateException");
		} catch (IllegalStateException e) {
			Assert.assertEquals("User already voted", e.getMessage());
		}
	}

	@Test
	public void voteHelpful_ShouldPropagateOriginalMessage_WhenFacadeThrowsIllegalStateException() {
		final String errorMessage = "User test@email.com already voted on review 123456789";
		doThrow(new IllegalStateException(errorMessage))
			.when(telefonicaReviewVoteFacade).voteHelpful(REVIEW_ID);

		try {
			controller.voteHelpful(REVIEW_ID);
		} catch (IllegalStateException e) {
			Assert.assertEquals(errorMessage, e.getMessage());
		}
	}

	@Test
	public void getDataMapper_ShouldReturnNull_WhenNotSet() {
		Assert.assertNull(controller.getDataMapper());
	}

	@Test
	public void getI18nService_ShouldReturnNull_WhenNotSet() {
		Assert.assertNull(controller.getI18nService());
	}

	@Test
	public void getMessageSource_ShouldReturnNull_WhenNotSet() {
		Assert.assertNull(controller.getMessageSource());
	}

	@Test
	public void setDataMapper_ShouldInjectDataMapper_WhenProvided() throws Exception {
		de.hybris.platform.webservicescommons.mapping.DataMapper dataMapper =
			mock(de.hybris.platform.webservicescommons.mapping.DataMapper.class);

		controller.setDataMapper(dataMapper);

		Assert.assertEquals(dataMapper, controller.getDataMapper());
	}

	@Test
	public void setI18nService_ShouldInjectI18NService_WhenProvided() {
		de.hybris.platform.servicelayer.i18n.I18NService i18nService =
			mock(de.hybris.platform.servicelayer.i18n.I18NService.class);

		controller.setI18nService(i18nService);

		Assert.assertEquals(i18nService, controller.getI18nService());
	}

	@Test
	public void setMessageSource_ShouldInjectMessageSource_WhenProvided() {
		org.springframework.context.MessageSource messageSource =
			mock(org.springframework.context.MessageSource.class);

		controller.setMessageSource(messageSource);

		Assert.assertEquals(messageSource, controller.getMessageSource());
	}
}
