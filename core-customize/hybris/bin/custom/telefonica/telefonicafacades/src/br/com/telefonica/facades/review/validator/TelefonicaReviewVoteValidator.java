package br.com.telefonica.facades.review.validator;

import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class TelefonicaReviewVoteValidator implements Validator {

	@Override
	public boolean supports(final Class<?> clazz) {
		return String.class.equals(clazz);
	}

	@Override
	public void validate(final Object target, final Errors errors) {
		if (StringUtils.isBlank((String) target)) {
			errors.reject("review.vote.reviewid.empty",
				"Review identifier must not be empty.");
		}
	}
}
