package br.com.telefonica.facades.review;

import br.com.telefonica.facades.review.data.ReviewVoteData;

public interface TelefonicaReviewVoteFacade {
	void voteHelpful(final String reviewCode);

	ReviewVoteData getVoteData(String reviewPk);
}
