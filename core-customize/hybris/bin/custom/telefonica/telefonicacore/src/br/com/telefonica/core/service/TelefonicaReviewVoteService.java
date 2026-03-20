package br.com.telefonica.core.service;

import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.customerreview.model.CustomerReviewModel;


public interface TelefonicaReviewVoteService {
	void vote(final CustomerReviewModel review, final UserModel voter);

	boolean hasVoted(final CustomerReviewModel review, final UserModel voter);

	CustomerReviewModel getReviewByPk(String reviewPk);

}
