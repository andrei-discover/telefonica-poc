package br.com.telefonica.core.moderation;

import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.customerreview.model.CustomerReviewModel;


public interface TelefonicaModerationService
{
	void approve(CustomerReviewModel review, String comment, UserModel moderator);

	void reject(CustomerReviewModel review, String comment, UserModel moderator);
}
