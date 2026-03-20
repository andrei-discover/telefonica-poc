package br.com.telefonica.facades.review.validator;

import de.hybris.bootstrap.annotations.UnitTest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class TelefonicaReviewVoteValidatorTest {

	private TelefonicaReviewVoteValidator validator;

	@Before
	public void setUp() {
		validator = new TelefonicaReviewVoteValidator();
	}

	@Test
	public void supports_ShouldReturnTrue_WhenClassIsString() {
		Assert.assertTrue(validator.supports(String.class));
	}

	@Test
	public void supports_ShouldReturnFalse_WhenClassIsInteger() {
		Assert.assertFalse(validator.supports(Integer.class));
	}

	@Test
	public void supports_ShouldReturnFalse_WhenClassIsObject() {
		Assert.assertFalse(validator.supports(Object.class));
	}

	@Test
	public void validate_ShouldNotHaveErrors_WhenReviewIdIsValid() {
		final Errors errors = new BeanPropertyBindingResult("123456789", "reviewId");

		validator.validate("123456789", errors);

		Assert.assertFalse(errors.hasErrors());
	}

	@Test
	public void validate_ShouldHaveErrors_WhenReviewIdIsNull() {
		final Errors errors = new BeanPropertyBindingResult(null, "reviewId");

		validator.validate(null, errors);

		Assert.assertTrue(errors.hasErrors());
		Assert.assertEquals("review.vote.reviewid.empty", errors.getAllErrors().get(0).getCode());
	}

	@Test
	public void validate_ShouldHaveErrors_WhenReviewIdIsEmpty() {
		final Errors errors = new BeanPropertyBindingResult("", "reviewId");

		validator.validate("", errors);

		Assert.assertTrue(errors.hasErrors());
		Assert.assertEquals("review.vote.reviewid.empty", errors.getAllErrors().get(0).getCode());
	}

	@Test
	public void validate_ShouldHaveErrors_WhenReviewIdIsBlank() {
		final Errors errors = new BeanPropertyBindingResult("   ", "reviewId");

		validator.validate("   ", errors);

		Assert.assertTrue(errors.hasErrors());
		Assert.assertEquals("review.vote.reviewid.empty", errors.getAllErrors().get(0).getCode());
	}

	@Test
	public void validate_ShouldHaveErrors_WhenReviewIdIsTab() {
		final Errors errors = new BeanPropertyBindingResult("\t", "reviewId");

		validator.validate("\t", errors);

		Assert.assertTrue(errors.hasErrors());
		Assert.assertEquals("review.vote.reviewid.empty", errors.getAllErrors().get(0).getCode());
	}

	@Test
	public void validate_ShouldNotHaveErrors_WhenReviewIdHasSpecialCharacters() {
		final Errors errors = new BeanPropertyBindingResult("abc-123_xyz", "reviewId");

		validator.validate("abc-123_xyz", errors);

		Assert.assertFalse(errors.hasErrors());
	}
}
