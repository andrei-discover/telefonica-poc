package br.com.telefonica.backoffice.actions;

import com.hybris.cockpitng.actions.ActionContext;
import com.hybris.cockpitng.actions.ActionResult;
import com.hybris.cockpitng.actions.CockpitAction;
import com.hybris.cockpitng.engine.impl.AbstractComponentWidgetAdapterAware;
import de.hybris.platform.customerreview.enums.CustomerReviewApprovalType;
import de.hybris.platform.customerreview.model.CustomerReviewModel;
import de.hybris.platform.servicelayer.user.UserService;

import javax.annotation.Resource;


public class TelefonicaApprovalReviewAction extends AbstractComponentWidgetAdapterAware implements CockpitAction<CustomerReviewModel, Object>
{
	private static final String OUTPUT_SOCKET = "moderationInput";

	@Resource
	private UserService userService;

	@Override
	public ActionResult<Object> perform(ActionContext<CustomerReviewModel> ctx)
	{
		final CustomerReviewModel item = ctx.getData();

		if (item == null)
		{
			return new ActionResult<>(ActionResult.ERROR);
		}

		sendOutput(OUTPUT_SOCKET, item);
		return new ActionResult<>(ActionResult.SUCCESS);
	}

	@Override
	public boolean canPerform(ActionContext<CustomerReviewModel> ctx)
	{
		return ctx.getData() != null && CustomerReviewApprovalType.PENDING.equals(ctx.getData().getApprovalStatus()) && userService.getCurrentUser()
		.getGroups()
		.stream()
		.anyMatch(group -> "moderationgroup".equals(group.getUid()));
	}

	@Override
	public boolean needsConfirmation(ActionContext<CustomerReviewModel> ctx)
	{
		return false;
	}
}
