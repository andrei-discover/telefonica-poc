package br.com.telefonica.facades.populators;

import br.com.telefonica.core.service.TelefonicaReviewVoteService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.ReviewData;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.customerreview.enums.CustomerReviewApprovalType;
import de.hybris.platform.customerreview.model.CustomerReviewModel;
import de.hybris.platform.servicelayer.user.UserService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class TelefonicaReviewPopulatorTest {

	private TelefonicaReviewPopulator populator;
	private UserService userService;
	private TelefonicaReviewVoteService telefonicaReviewVoteService;
	private UserModel currentUser;

	@Before
	public void setUp() {
		populator = new TelefonicaReviewPopulator();
		userService = mock(UserService.class);
		telefonicaReviewVoteService = mock(TelefonicaReviewVoteService.class);
		currentUser = mock(UserModel.class);

		populator.setUserService(userService);
		populator.setTelefonicaReviewVoteService(telefonicaReviewVoteService);

		when(userService.getCurrentUser()).thenReturn(currentUser);
	}

	@Test(expected = NullPointerException.class)
	public void populate_ShouldThrowNullPointerException_WhenSourceIsNull() {
		populator.populate(null, new ReviewData());
	}

	@Test(expected = NullPointerException.class)
	public void populate_ShouldThrowNullPointerException_WhenTargetIsNull() {
		CustomerReviewModel source = mock(CustomerReviewModel.class);
		populator.populate(source, null);
	}

	@Test
	public void populate_ShouldSetUsefulCount_WhenSourceHasValue() {
		CustomerReviewModel source = mock(CustomerReviewModel.class);
		when(source.getUsefulCount()).thenReturn(5);

		ReviewData target = new ReviewData();
		populator.populate(source, target);

		Assert.assertEquals(Integer.valueOf(5), target.getUsefulCount());
	}

	@Test
	public void populate_ShouldSetUsefulCountToZero_WhenSourceReturnsNull() {
		CustomerReviewModel source = mock(CustomerReviewModel.class);
		when(source.getUsefulCount()).thenReturn(null);

		ReviewData target = new ReviewData();
		populator.populate(source, target);

		Assert.assertEquals(Integer.valueOf(0), target.getUsefulCount());
	}

	@Test
	public void populate_ShouldSetUsefulCountToZero_WhenSourceReturnsZero() {
		CustomerReviewModel source = mock(CustomerReviewModel.class);
		when(source.getUsefulCount()).thenReturn(0);

		ReviewData target = new ReviewData();
		populator.populate(source, target);

		Assert.assertEquals(Integer.valueOf(0), target.getUsefulCount());
	}

	@Test
	public void populate_ShouldSetUserMarkedUsefulTrue_WhenCurrentUserHasVoted() {
		CustomerReviewModel source = mock(CustomerReviewModel.class);
		when(telefonicaReviewVoteService.hasVoted(source, currentUser)).thenReturn(true);

		ReviewData target = new ReviewData();
		populator.populate(source, target);

		Assert.assertTrue(target.getUserMarkedUseful());
	}

	@Test
	public void populate_ShouldSetUserMarkedUsefulFalse_WhenCurrentUserHasNotVoted() {
		CustomerReviewModel source = mock(CustomerReviewModel.class);
		when(telefonicaReviewVoteService.hasVoted(source, currentUser)).thenReturn(false);

		ReviewData target = new ReviewData();
		populator.populate(source, target);

		Assert.assertFalse(target.getUserMarkedUseful());
	}

	@Test
	public void populate_ShouldDelegateToVoteService_WithCurrentUser() {
		CustomerReviewModel source = mock(CustomerReviewModel.class);

		populator.populate(source, new ReviewData());

		verify(telefonicaReviewVoteService).hasVoted(source, currentUser);
	}

	@Test
	public void populate_ShouldSetVerifiedPurchaseTrue_WhenSourceReturnsTrue() {
		CustomerReviewModel source = mock(CustomerReviewModel.class);
		when(source.getVerifiedPurchase()).thenReturn(Boolean.TRUE);

		ReviewData target = new ReviewData();
		populator.populate(source, target);

		Assert.assertTrue(target.getVerifiedPurchase());
	}

	@Test
	public void populate_ShouldSetVerifiedPurchaseFalse_WhenSourceReturnsFalse() {
		CustomerReviewModel source = mock(CustomerReviewModel.class);
		when(source.getVerifiedPurchase()).thenReturn(Boolean.FALSE);

		ReviewData target = new ReviewData();
		populator.populate(source, target);

		Assert.assertFalse(target.getVerifiedPurchase());
	}

	@Test
	public void populate_ShouldSetVerifiedPurchaseFalse_WhenSourceReturnsNull() {
		CustomerReviewModel source = mock(CustomerReviewModel.class);
		when(source.getVerifiedPurchase()).thenReturn(null);

		ReviewData target = new ReviewData();
		populator.populate(source, target);

		Assert.assertFalse(target.getVerifiedPurchase());
	}

	@Test
	public void populate_ShouldSetApprovalStatus_WhenStatusIsApproved() {
		CustomerReviewModel source = mock(CustomerReviewModel.class);
		when(source.getApprovalStatus()).thenReturn(CustomerReviewApprovalType.APPROVED);

		ReviewData target = new ReviewData();
		populator.populate(source, target);

		Assert.assertEquals(CustomerReviewApprovalType.APPROVED.getCode(), target.getApprovalStatus());
	}

	@Test
	public void populate_ShouldSetApprovalStatus_WhenStatusIsPending() {
		CustomerReviewModel source = mock(CustomerReviewModel.class);
		when(source.getApprovalStatus()).thenReturn(CustomerReviewApprovalType.PENDING);

		ReviewData target = new ReviewData();
		populator.populate(source, target);

		Assert.assertEquals(CustomerReviewApprovalType.PENDING.getCode(), target.getApprovalStatus());
	}

	@Test
	public void populate_ShouldSetApprovalStatus_WhenStatusIsRejected() {
		CustomerReviewModel source = mock(CustomerReviewModel.class);
		when(source.getApprovalStatus()).thenReturn(CustomerReviewApprovalType.REJECTED);

		ReviewData target = new ReviewData();
		populator.populate(source, target);

		Assert.assertEquals(CustomerReviewApprovalType.REJECTED.getCode(), target.getApprovalStatus());
	}

	@Test
	public void populate_ShouldNotSetApprovalStatus_WhenStatusIsNull() {
		CustomerReviewModel source = mock(CustomerReviewModel.class);
		when(source.getApprovalStatus()).thenReturn(null);

		ReviewData target = new ReviewData();
		populator.populate(source, target);

		Assert.assertNull(target.getApprovalStatus());
	}

	@Test
	public void populate_ShouldMapAllFields_WhenSourceIsComplete() {
		CustomerReviewModel source = mock(CustomerReviewModel.class);
		when(source.getUsefulCount()).thenReturn(3);
		when(source.getVerifiedPurchase()).thenReturn(Boolean.TRUE);
		when(source.getApprovalStatus()).thenReturn(CustomerReviewApprovalType.APPROVED);
		when(telefonicaReviewVoteService.hasVoted(source, currentUser)).thenReturn(true);

		ReviewData target = new ReviewData();
		populator.populate(source, target);

		Assert.assertEquals(Integer.valueOf(3), target.getUsefulCount());
		Assert.assertTrue(target.getUserMarkedUseful());
		Assert.assertTrue(target.getVerifiedPurchase());
		Assert.assertEquals(CustomerReviewApprovalType.APPROVED.getCode(), target.getApprovalStatus());
	}

	@Test
	public void populate_ShouldUseDefaultValues_WhenSourceFieldsAreNull() {
		CustomerReviewModel source = mock(CustomerReviewModel.class);
		when(source.getUsefulCount()).thenReturn(null);
		when(source.getVerifiedPurchase()).thenReturn(null);
		when(source.getApprovalStatus()).thenReturn(null);
		when(telefonicaReviewVoteService.hasVoted(source, currentUser)).thenReturn(false);

		ReviewData target = new ReviewData();
		populator.populate(source, target);

		Assert.assertEquals(Integer.valueOf(0), target.getUsefulCount());
		Assert.assertFalse(target.getUserMarkedUseful());
		Assert.assertFalse(target.getVerifiedPurchase());
		Assert.assertNull(target.getApprovalStatus());
	}

	@Test
	public void populate_ShouldConvertAllApprovalStatusValues() {
		for (final CustomerReviewApprovalType status : CustomerReviewApprovalType.values()) {
			CustomerReviewModel source = mock(CustomerReviewModel.class);
			when(source.getApprovalStatus()).thenReturn(status);

			ReviewData target = new ReviewData();
			populator.populate(source, target);

			Assert.assertEquals(status.getCode(), target.getApprovalStatus());
		}
	}
	@Test
	public void setUserService_ShouldUseNewUserService_WhenReplaced() {
		UserModel otherUser = mock(UserModel.class);
		UserService otherUserService = mock(UserService.class);
		when(otherUserService.getCurrentUser()).thenReturn(otherUser);
		when(telefonicaReviewVoteService.hasVoted(Mockito.any(), Mockito.eq(otherUser))).thenReturn(true);

		populator.setUserService(otherUserService);

		CustomerReviewModel source = mock(CustomerReviewModel.class);
		ReviewData target = new ReviewData();
		populator.populate(source, target);

		verify(otherUserService).getCurrentUser();
		Assert.assertTrue(target.getUserMarkedUseful());
	}

	@Test
	public void setTelefonicaReviewVoteService_ShouldUseNewService_WhenReplaced() {
		TelefonicaReviewVoteService otherVoteService = mock(TelefonicaReviewVoteService.class);
		when(userService.getCurrentUser()).thenReturn(mock(UserModel.class));
		when(otherVoteService.hasVoted(Mockito.any(), Mockito.any())).thenReturn(true);

		populator.setTelefonicaReviewVoteService(otherVoteService);

		CustomerReviewModel source = mock(CustomerReviewModel.class);
		ReviewData target = new ReviewData();
		populator.populate(source, target);

		verify(otherVoteService).hasVoted(Mockito.any(), Mockito.any());
		Assert.assertTrue(target.getUserMarkedUseful());
	}
}
