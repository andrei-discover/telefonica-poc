package br.com.telefonica.backoffice.controller;

import br.com.telefonica.core.moderation.TelefonicaModerationService;
import com.hybris.cockpitng.annotations.SocketEvent;
import com.hybris.cockpitng.annotations.ViewEvent;
import com.hybris.cockpitng.util.DefaultWidgetController;
import de.hybris.platform.customerreview.model.CustomerReviewModel;
import de.hybris.platform.servicelayer.user.UserService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;

import java.util.HashMap;
import java.util.Map;


public class TelefonicaModerationController extends DefaultWidgetController
{
	private static final Logger LOG = LoggerFactory.getLogger(TelefonicaModerationController.class);

	private static final String OUTPUT_SOCKET = "moderationOutput";

	private static final String MODEL_KEY_ITEM    = "currentItem";
	private static final String DECISION_APPROVE  = "APPROVE";
	private static final String DECISION_REJECT   = "REJECT";

	@Wire
	private Textbox moderationComment;
	@Wire
	private Label commentError;
	@Wire
	private Button approveButton;
	@Wire
	private Button rejectButton;
	@WireVariable
	private TelefonicaModerationService telefonicaModerationService;
	@WireVariable
	private UserService userService;

	@Override
	public void initialize(final org.zkoss.zk.ui.Component comp)
	{
		super.initialize(comp);

		commentError.setVisible(false);

		LOG.info("ModerationController initialized.");
	}

	@ViewEvent(componentID = "approveButton", eventName = Events.ON_CLICK)
	public void onApprove()
	{
		LOG.info("Moderation decision: APPROVE");
		submitDecision(DECISION_APPROVE);
	}

	@ViewEvent(componentID = "rejectButton", eventName = Events.ON_CLICK)
	public void onReject()
	{
		final String comment = moderationComment.getValue();

		if (StringUtils.isBlank(comment))
		{
			commentError.setVisible(true);
			moderationComment.setFocus(true);
			LOG.warn("REJECT attempted without a comment – validation failed.");
			return;
		}

		commentError.setVisible(false);
		LOG.info("Moderation decision: REJECT");
		submitDecision(DECISION_REJECT);
	}

	@ViewEvent(componentID = "moderationComment", eventName = Events.ON_CHANGING)
	public void onCommentChanging()
	{
		if (commentError.isVisible())
		{
			commentError.setVisible(false);
		}
	}

	@SocketEvent(socketId = "moderationInput")
	public void onItemReceived(final CustomerReviewModel item)
	{
		getModel().setValue(MODEL_KEY_ITEM, item);
		LOG.info("Item received for moderation: {}", item.getPk());
	}

	@ViewEvent(componentID = "cancelButton", eventName = Events.ON_CLICK)
	public void onCancel()
	{
		LOG.info("Moderation modal cancelled by user.");
		getModel().setValue(MODEL_KEY_ITEM, null);
		sendOutput(OUTPUT_SOCKET, null);
	}

	private void submitDecision(final String decision)
	{
		final CustomerReviewModel item = getModel().getValue(MODEL_KEY_ITEM, CustomerReviewModel.class);
		final String moderationNotes = StringUtils.trimToEmpty(moderationComment.getValue());

		if (DECISION_APPROVE.equals(decision))
		{
			telefonicaModerationService.approve(item, moderationNotes, userService.getCurrentUser());
		}
		else if (DECISION_REJECT.equals(decision))
		{
			telefonicaModerationService.reject(item, moderationNotes, userService.getCurrentUser());
		}

		sendOutput(OUTPUT_SOCKET, buildOutput(decision, moderationNotes, item));
	}

	private Map<String, Object> buildOutput(final String decision, final String comment, final CustomerReviewModel item)
	{
		final Map<String, Object> output = new HashMap<>();
		output.put("decision", decision);
		output.put("comment", comment);
		output.put("item", item);
		return output;
	}
}
