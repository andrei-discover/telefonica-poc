package br.com.telefonica.backoffice.controller;

import br.com.telefonica.core.model.ProductQuestionModel;
import br.com.telefonica.core.moderation.TelefonicaModerationService;
import com.hybris.cockpitng.annotations.SocketEvent;
import com.hybris.cockpitng.annotations.ViewEvent;
import com.hybris.cockpitng.util.DefaultWidgetController;
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

public class TelefonicaQuestionModerationController extends DefaultWidgetController
{
	private static final Logger LOG = LoggerFactory.getLogger(TelefonicaQuestionModerationController.class);

	private static final String OUTPUT_SOCKET = "questionModerationOutput";
	private static final String MODEL_KEY_ITEM = "currentItem";
	private static final String DECISION_PUBLISH = "PUBLISH";
	private static final String DECISION_REJECT = "REJECT";

	@Wire
	private Label questionText;
	@Wire
	private Textbox moderationAnswer;
	@Wire
	private Textbox moderationComment;
	@Wire
	private Label answerError;
	@Wire
	private Label commentError;
	@Wire
	private Button publishButton;
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
		answerError.setVisible(false);
		commentError.setVisible(false);
		LOG.info("QuestionModerationController initialized.");
	}

	@SocketEvent(socketId = "questionModerationInput")
	public void onQuestionReceived(final ProductQuestionModel question)
	{
		getModel().setValue(MODEL_KEY_ITEM, question);

		questionText.setValue(question.getQuestion());

		LOG.info("Question received for moderation: {}", question.getPk());
	}

	@ViewEvent(componentID = "publishButton", eventName = Events.ON_CLICK)
	public void onPublish()
	{
		if (StringUtils.isBlank(moderationAnswer.getValue()))
		{
			answerError.setVisible(true);
			moderationAnswer.setFocus(true);
			LOG.warn("PUBLISH attempted without an answer — validation failed.");
			return;
		}

		answerError.setVisible(false);
		LOG.info("Question moderation decision: PUBLISH");
		submitDecision(DECISION_PUBLISH);
	}

	@ViewEvent(componentID = "rejectButton", eventName = Events.ON_CLICK)
	public void onReject()
	{
		if (StringUtils.isBlank(moderationComment.getValue()))
		{
			commentError.setVisible(true);
			moderationComment.setFocus(true);
			LOG.warn("REJECT attempted without a comment — validation failed.");
			return;
		}

		commentError.setVisible(false);
		LOG.info("Question moderation decision: REJECT");
		submitDecision(DECISION_REJECT);
	}

	@ViewEvent(componentID = "moderationAnswer", eventName = Events.ON_CHANGING)
	public void onAnswerChanging()
	{
		if (answerError.isVisible())
		{
			answerError.setVisible(false);
		}
	}

	@ViewEvent(componentID = "moderationComment", eventName = Events.ON_CHANGING)
	public void onCommentChanging()
	{
		if (commentError.isVisible())
		{
			commentError.setVisible(false);
		}
	}

	@ViewEvent(componentID = "cancelButton", eventName = Events.ON_CLICK)
	public void onCancel()
	{
		LOG.info("Question moderation modal cancelled by user.");
		getModel().setValue(MODEL_KEY_ITEM, null);
		sendOutput(OUTPUT_SOCKET, null);
	}

	private void submitDecision(final String decision)
	{
		final ProductQuestionModel question = getModel()
			.getValue(MODEL_KEY_ITEM, ProductQuestionModel.class);
		final String answer = StringUtils.trimToEmpty(moderationAnswer.getValue());
		final String notes = StringUtils.trimToEmpty(moderationComment.getValue());

		if (DECISION_PUBLISH.equals(decision))
		{
			telefonicaModerationService.approveQuestion(question, answer, notes,
				userService.getCurrentUser());
		}
		else
		{
			telefonicaModerationService.rejectQuestion(question, answer, notes,
				userService.getCurrentUser());
		}

		sendOutput(OUTPUT_SOCKET, buildOutput(decision, answer, notes, question));
	}

	private Map<String, Object> buildOutput(final String decision,
		final String answer,
		final String notes,
		final ProductQuestionModel question)
	{
		final Map<String, Object> output = new HashMap<>();
		output.put("decision", decision);
		output.put("answer", answer);
		output.put("notes", notes);
		output.put("item", question);
		return output;
	}
}
