package br.com.telefonica.core.moderation;

import br.com.telefonica.core.model.ProductQuestionModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.customerreview.model.CustomerReviewModel;


public interface TelefonicaModerationService
{
	void approve(CustomerReviewModel review, String comment, UserModel moderator);

	void reject(CustomerReviewModel review, String comment, UserModel moderator);

	void approveQuestion(ProductQuestionModel question, String answer, String moderationNotes, UserModel userModel);

	void rejectQuestion(ProductQuestionModel question, String answer, String moderationNotes, UserModel userModel);
}
