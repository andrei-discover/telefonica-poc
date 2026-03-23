package br.com.telefonica.core.moderation;

import static org.mockito.Mockito.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import br.com.telefonica.core.enums.QuestionStatus;
import br.com.telefonica.core.model.ProductQuestionModel;
import br.com.telefonica.core.moderation.impl.DefaultTelefonicaModerationService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.customerreview.enums.CustomerReviewApprovalType;
import de.hybris.platform.customerreview.model.CustomerReviewModel;
import de.hybris.platform.servicelayer.model.ModelService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultTelefonicaModerationServiceTest
{
	private static final String COMMENT = "Conteúdo aprovado pelo moderador";
	private static final String ANSWER = "Sim, o produto possui garantia de 1 ano.";
	private static final String MODERATION_NOTES = "Pergunta aprovada";

	@Mock
	private ModelService modelService;

	@InjectMocks
	private DefaultTelefonicaModerationService moderationService;

	private CustomerReviewModel review;
	private ProductQuestionModel question;
	private EmployeeModel moderator;

	@Before
	public void setUp()
	{
		moderator = new EmployeeModel();
		moderator.setUid("moderator@telcospa.com");

		review = new CustomerReviewModel();
		review.setRating(5.0);

		question = new ProductQuestionModel();
		question.setQuestion("O produto tem garantia?");
	}

	@Test
	public void testApproveReview()
	{
		// When
		moderationService.approve(review, COMMENT, moderator);

		// Then
		assertEquals(CustomerReviewApprovalType.APPROVED, review.getApprovalStatus());
		assertEquals(COMMENT, review.getModerationNotes());
		assertEquals(moderator, review.getModerator());
		assertNotNull(review.getModeratedAt());
		verify(modelService).save(review);
	}

	@Test
	public void testApproveReviewWithEmptyComment()
	{
		// When
		moderationService.approve(review, "", moderator);

		// Then
		assertEquals(CustomerReviewApprovalType.APPROVED, review.getApprovalStatus());
		assertEquals("", review.getModerationNotes());
		verify(modelService).save(review);
	}

	@Test
	public void testApproveReviewWithNullComment()
	{
		// When
		moderationService.approve(review, null, moderator);

		// Then
		assertEquals(CustomerReviewApprovalType.APPROVED, review.getApprovalStatus());
		verify(modelService).save(review);
	}

	@Test
	public void testRejectReview()
	{
		// When
		moderationService.reject(review, COMMENT, moderator);

		// Then
		assertEquals(CustomerReviewApprovalType.REJECTED, review.getApprovalStatus());
		assertEquals(COMMENT, review.getModerationNotes());
		assertEquals(moderator, review.getModerator());
		assertNotNull(review.getModeratedAt());
		verify(modelService).save(review);
	}

	@Test
	public void testRejectReviewSetsModeratedAt()
	{
		// When
		moderationService.reject(review, COMMENT, moderator);

		// Then
		assertNotNull(review.getModeratedAt());
		verify(modelService).save(review);
	}

	@Test
	public void testApproveQuestion()
	{
		// When
		moderationService.approveQuestion(question, ANSWER, MODERATION_NOTES, moderator);

		// Then
		assertEquals(QuestionStatus.APPROVED, question.getStatus());
		assertEquals(ANSWER, question.getAnswer());
		assertEquals(moderator, question.getAnswerModerator());
		assertEquals(MODERATION_NOTES, question.getModerationNotes());
		assertNotNull(question.getAnswerModeratedAt());
		verify(modelService).save(question);
	}

	@Test
	public void testApproveQuestionWithEmptyAnswer()
	{
		// When
		moderationService.approveQuestion(question, "", MODERATION_NOTES, moderator);

		// Then
		assertEquals(QuestionStatus.APPROVED, question.getStatus());
		assertEquals("", question.getAnswer());
		verify(modelService).save(question);
	}

	@Test
	public void testApproveQuestionSetsAnswerModerator()
	{
		// When
		moderationService.approveQuestion(question, ANSWER, MODERATION_NOTES, moderator);

		// Then
		assertEquals(moderator, question.getAnswerModerator());
		assertNotNull(question.getAnswerModeratedAt());
		verify(modelService).save(question);
	}

	@Test
	public void testRejectQuestion()
	{
		// When
		moderationService.rejectQuestion(question, ANSWER, MODERATION_NOTES, moderator);

		// Then
		assertEquals(QuestionStatus.REJECTED, question.getStatus());
		assertEquals(ANSWER, question.getAnswer());
		assertEquals(moderator, question.getAnswerModerator());
		assertEquals(MODERATION_NOTES, question.getModerationNotes());
		assertNotNull(question.getAnswerModeratedAt());
		verify(modelService).save(question);
	}

	@Test
	public void testRejectQuestionWithNullNotes()
	{
		// When
		moderationService.rejectQuestion(question, ANSWER, null, moderator);

		// Then
		assertEquals(QuestionStatus.REJECTED, question.getStatus());
		verify(modelService).save(question);
	}

	@Test
	public void testRejectQuestionSetsAnswerModeratedAt()
	{
		// When
		moderationService.rejectQuestion(question, ANSWER, MODERATION_NOTES, moderator);

		// Then
		assertNotNull(question.getAnswerModeratedAt());
		verify(modelService).save(question);
	}
}
