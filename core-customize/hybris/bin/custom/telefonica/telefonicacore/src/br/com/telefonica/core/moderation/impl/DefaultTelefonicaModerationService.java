package br.com.telefonica.core.moderation.impl;

import br.com.telefonica.core.enums.QuestionStatus;
import br.com.telefonica.core.model.ProductQuestionModel;
import br.com.telefonica.core.moderation.TelefonicaModerationService;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.customerreview.enums.CustomerReviewApprovalType;
import de.hybris.platform.customerreview.model.CustomerReviewModel;
import de.hybris.platform.servicelayer.model.ModelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;


public class DefaultTelefonicaModerationService implements TelefonicaModerationService
{
	private static final Logger LOG = LoggerFactory.getLogger(DefaultTelefonicaModerationService.class);

	private ModelService modelService;

	@Override
	public void approve(final CustomerReviewModel review, final String comment, final UserModel moderator)
	{
		applyDecision(review, CustomerReviewApprovalType.APPROVED, comment, moderator);
	}

	@Override
	public void reject(final CustomerReviewModel review, final String comment, final UserModel moderator)
	{
		applyDecision(review, CustomerReviewApprovalType.REJECTED, comment, moderator);
	}

	private void applyDecision(final CustomerReviewModel review, final CustomerReviewApprovalType status, final String comment,
		final UserModel userModel)
	{
		review.setApprovalStatus(status);
		review.setModerationNotes(comment);
		review.setModerator((EmployeeModel) userModel);
		review.setModeratedAt(new Date());
		modelService.save(review);

		LOG.info("Review {} {} by {} at {} — comment: {}", review.getPk(), status, userModel.getUid(), new Date(), comment);
	}

	@Override
	public void approveQuestion(final ProductQuestionModel question, final String answer, final String moderationNotes,
		UserModel userModel)
	{
		question.setStatus(QuestionStatus.APPROVED);
		question.setAnswer(answer);
		question.setAnswerModerator((EmployeeModel) userModel);
		question.setAnswerModeratedAt(new Date());
		question.setModerationNotes(moderationNotes);
		modelService.save(question);

		LOG.info("Question {} approved by {} — answer: {} notes: {}", question.getPk(), userModel.getUid(),
			answer, moderationNotes);
	}

	@Override
	public void rejectQuestion(final ProductQuestionModel question, final String answer, final String moderationNotes,
		UserModel userModel)
	{
		question.setStatus(QuestionStatus.REJECTED);
		question.setAnswer(answer);
		question.setAnswerModerator((EmployeeModel) userModel);
		question.setAnswerModeratedAt(new Date());
		question.setModerationNotes(moderationNotes);
		modelService.save(question);

		LOG.info("Question {} rejected by {} — notes: {}", question.getPk(), userModel.getUid(),
			moderationNotes);
	}

	public void setModelService(ModelService modelService)
	{
		this.modelService = modelService;
	}
}
