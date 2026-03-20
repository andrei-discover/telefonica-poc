package br.com.telefonica.facades.review.impl;

import br.com.telefonica.core.service.TelefonicaReviewVoteService;
import br.com.telefonica.facades.review.TelefonicaReviewVoteFacade;
import br.com.telefonica.facades.review.data.ReviewVoteData;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.customerreview.model.CustomerReviewModel;
import de.hybris.platform.servicelayer.user.UserService;


public class DefaultTelefonicaReviewVoteFacade implements TelefonicaReviewVoteFacade {
	private TelefonicaReviewVoteService telefonicaReviewVoteService;
	private UserService userService;

	@Override
	public void voteHelpful(final String reviewCode)
	{
		final UserModel currentUser = userService.getCurrentUser();
		final CustomerReviewModel review = telefonicaReviewVoteService.getReviewByPk(reviewCode);

		telefonicaReviewVoteService.vote(review, currentUser);
	}

	@Override
	public ReviewVoteData getVoteData(final String reviewPk) {
		final ReviewVoteData reviewVoteData = new ReviewVoteData();
		reviewVoteData.setVoted(hasCurrentUserVoted(reviewPk));
		reviewVoteData.setTotalVotes(getVoteCount(reviewPk));
		return reviewVoteData;
	}

	private boolean hasCurrentUserVoted(final String reviewPk) {
		final CustomerReviewModel review = telefonicaReviewVoteService.getReviewByPk(reviewPk);
		final UserModel currentUser = userService.getCurrentUser();
		return telefonicaReviewVoteService.hasVoted(review, currentUser);
	}

	private int getVoteCount(final String reviewPk) {
		final CustomerReviewModel review = telefonicaReviewVoteService.getReviewByPk(reviewPk);
		return review.getUsefulCount() != null ? review.getUsefulCount() : 0;
	}

	public void setTelefonicaReviewVoteService(TelefonicaReviewVoteService telefonicaReviewVoteService)
	{
		this.telefonicaReviewVoteService = telefonicaReviewVoteService;
	}

	public void setUserService(UserService userService)
	{
		this.userService = userService;
	}
}
