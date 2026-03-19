package br.com.telefonica.facades.populators;

import br.com.telefonica.core.service.TelefonicaReviewVoteService;
import de.hybris.platform.commercefacades.product.data.ReviewData;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.customerreview.model.CustomerReviewModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Objects;

public class TelefonicaReviewPopulator implements Populator<CustomerReviewModel, ReviewData> {
	private UserService userService;
	private TelefonicaReviewVoteService telefonicaReviewVoteService;

	@Override
	public void populate(final CustomerReviewModel source, final ReviewData target)
		throws ConversionException {

		Objects.requireNonNull(source, "source must not be null");
		Objects.requireNonNull(target, "target must not be null");


		target.setUsefulCount(Objects.nonNull(source.getUsefulCount()) ? source.getUsefulCount() : 0);
		target.setUserMarkedUseful(hasCurrentUserMarkedUseful(source));

		target.setVerifiedPurchase(Boolean.TRUE.equals(source.getVerifiedPurchase()));

		if (source.getApprovalStatus() != null) {
			target.setApprovalStatus(source.getApprovalStatus().getCode());
		}


	}
	private boolean hasCurrentUserMarkedUseful(final CustomerReviewModel review) {
		final UserModel currentUser = userService.getCurrentUser();
		return telefonicaReviewVoteService.hasVoted(review, currentUser);
	}

	public void setUserService(UserService userService)
	{
		this.userService = userService;
	}

	public void setTelefonicaReviewVoteService(TelefonicaReviewVoteService telefonicaReviewVoteService)
	{
		this.telefonicaReviewVoteService = telefonicaReviewVoteService;
	}
}
