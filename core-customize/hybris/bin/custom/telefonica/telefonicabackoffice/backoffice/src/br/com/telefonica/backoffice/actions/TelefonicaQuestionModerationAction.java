package br.com.telefonica.backoffice.actions;

import br.com.telefonica.core.model.ProductQuestionModel;
import br.com.telefonica.core.enums.QuestionStatus;
import com.hybris.cockpitng.actions.ActionContext;
import com.hybris.cockpitng.actions.ActionResult;
import com.hybris.cockpitng.actions.CockpitAction;
import com.hybris.cockpitng.engine.impl.AbstractComponentWidgetAdapterAware;
import de.hybris.platform.servicelayer.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;


public class TelefonicaQuestionModerationAction extends AbstractComponentWidgetAdapterAware
	implements CockpitAction<ProductQuestionModel, ProductQuestionModel>
{
	private static final Logger LOG = LoggerFactory.getLogger(TelefonicaQuestionModerationAction.class);
	private static final String OUTPUT_SOCKET = "questionModerationInput";

	@Resource
	private UserService userService;

	@Override
	public ActionResult<ProductQuestionModel> perform(final ActionContext<ProductQuestionModel> ctx)
	{
		final ProductQuestionModel question = ctx.getData();
		if (question == null)
		{
			return new ActionResult<>(ActionResult.ERROR);
		}

		LOG.info("Sending question {} for moderation", question.getPk());
		sendOutput(OUTPUT_SOCKET, question);

		return new ActionResult<>(ActionResult.SUCCESS);
	}

	@Override
	public boolean canPerform(final ActionContext<ProductQuestionModel> ctx)
	{
		final ProductQuestionModel question = ctx.getData();
		if (question == null)
		{
			return false;
		}

		if (!QuestionStatus.PENDING.equals(question.getStatus()))
		{
			return false;
		}

		return userService.getCurrentUser().getGroups().stream().anyMatch(group -> "moderationgroup".equals(group.getUid()));
	}
}
