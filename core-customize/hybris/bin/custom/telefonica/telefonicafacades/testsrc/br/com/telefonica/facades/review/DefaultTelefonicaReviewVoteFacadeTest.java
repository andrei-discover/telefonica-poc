package br.com.telefonica.facades.review;

import br.com.telefonica.core.service.TelefonicaReviewVoteService;
import br.com.telefonica.facades.review.data.ReviewVoteData;
import br.com.telefonica.facades.review.impl.DefaultTelefonicaReviewVoteFacade;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.customerreview.model.CustomerReviewModel;
import de.hybris.platform.servicelayer.user.UserService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultTelefonicaReviewVoteFacadeTest {

	private DefaultTelefonicaReviewVoteFacade facade;
	private TelefonicaReviewVoteService telefonicaReviewVoteService;
	private UserService userService;
	private CustomerReviewModel review;
	private UserModel currentUser;

	private static final String REVIEW_PK = "123456789";

	@Before
	public void setUp() {
		facade                       = new DefaultTelefonicaReviewVoteFacade();
		telefonicaReviewVoteService  = mock(TelefonicaReviewVoteService.class);
		userService                  = mock(UserService.class);
		review                       = mock(CustomerReviewModel.class);
		currentUser                  = mock(UserModel.class);

		facade.setTelefonicaReviewVoteService(telefonicaReviewVoteService);
		facade.setUserService(userService);

		when(userService.getCurrentUser()).thenReturn(currentUser);
		when(telefonicaReviewVoteService.getReviewByPk(REVIEW_PK)).thenReturn(review);
	}

	@Test
	public void voteHelpful_ShouldGetCurrentUser_WhenCalled() {
		facade.voteHelpful(REVIEW_PK);

		verify(userService).getCurrentUser();
	}

	@Test
	public void voteHelpful_ShouldGetReviewByPk_WhenCalled() {
		facade.voteHelpful(REVIEW_PK);

		verify(telefonicaReviewVoteService).getReviewByPk(REVIEW_PK);
	}

	@Test
	public void voteHelpful_ShouldDelegateVoteToService_WhenCalled() {
		facade.voteHelpful(REVIEW_PK);

		verify(telefonicaReviewVoteService).vote(review, currentUser);
	}

	@Test
	public void getVoteData_ShouldReturnReviewVoteData_WhenCalled() {
		when(telefonicaReviewVoteService.hasVoted(review, currentUser)).thenReturn(false);
		when(review.getUsefulCount()).thenReturn(0);

		ReviewVoteData result = facade.getVoteData(REVIEW_PK);

		Assert.assertNotNull(result);
	}

	@Test
	public void getVoteData_ShouldSetVotedTrue_WhenCurrentUserHasVoted() {
		when(telefonicaReviewVoteService.hasVoted(review, currentUser)).thenReturn(true);
		when(review.getUsefulCount()).thenReturn(1);

		ReviewVoteData result = facade.getVoteData(REVIEW_PK);

		Assert.assertTrue(result.getVoted());
	}

	@Test
	public void getVoteData_ShouldSetVotedFalse_WhenCurrentUserHasNotVoted() {
		when(telefonicaReviewVoteService.hasVoted(review, currentUser)).thenReturn(false);
		when(review.getUsefulCount()).thenReturn(0);

		ReviewVoteData result = facade.getVoteData(REVIEW_PK);

		Assert.assertFalse(result.getVoted());
	}

	@Test
	public void getVoteData_ShouldSetTotalVotes_WhenUsefulCountIsNotNull() {
		when(telefonicaReviewVoteService.hasVoted(review, currentUser)).thenReturn(false);
		when(review.getUsefulCount()).thenReturn(5);

		ReviewVoteData result = facade.getVoteData(REVIEW_PK);

		Assert.assertEquals(5, (int) result.getTotalVotes());
	}

	@Test
	public void getVoteData_ShouldSetTotalVotesToZero_WhenUsefulCountIsNull() {
		when(telefonicaReviewVoteService.hasVoted(review, currentUser)).thenReturn(false);
		when(review.getUsefulCount()).thenReturn(null);

		ReviewVoteData result = facade.getVoteData(REVIEW_PK);

		Assert.assertEquals(0, (int) result.getTotalVotes());
	}

	@Test
	public void getVoteData_ShouldCallGetReviewByPk_WhenCalled() {
		when(telefonicaReviewVoteService.hasVoted(review, currentUser)).thenReturn(false);
		when(review.getUsefulCount()).thenReturn(0);

		facade.getVoteData(REVIEW_PK);

		verify(telefonicaReviewVoteService, org.mockito.Mockito.atLeastOnce()).getReviewByPk(REVIEW_PK);
	}

	@Test
	public void setTelefonicaReviewVoteService_ShouldUseNewService_WhenReplaced() {
		TelefonicaReviewVoteService otherService = mock(TelefonicaReviewVoteService.class);
		when(otherService.getReviewByPk(REVIEW_PK)).thenReturn(review);

		facade.setTelefonicaReviewVoteService(otherService);
		facade.voteHelpful(REVIEW_PK);

		verify(otherService).vote(review, currentUser);
		verify(telefonicaReviewVoteService, never()).vote(review, currentUser);
	}

	@Test
	public void setUserService_ShouldUseNewUserService_WhenReplaced() {
		UserService otherUserService = mock(UserService.class);
		UserModel otherUser = mock(UserModel.class);
		when(otherUserService.getCurrentUser()).thenReturn(otherUser);

		facade.setUserService(otherUserService);
		facade.voteHelpful(REVIEW_PK);

		verify(otherUserService).getCurrentUser();
		verify(userService, never()).getCurrentUser();
	}
}
