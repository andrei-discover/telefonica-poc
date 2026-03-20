package br.com.telefonica.core.service;

import br.com.telefonica.core.model.CustomerReviewVoteModel;
import br.com.telefonica.core.service.impl.DefaultTelefonicaReviewVoteService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.customerreview.model.CustomerReviewModel;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;
import de.hybris.platform.servicelayer.model.ModelService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultTelefonicaReviewVoteServiceTest {

	private DefaultTelefonicaReviewVoteService service;
	private ModelService modelService;
	private GenericDao<CustomerReviewVoteModel> customerReviewVoteGenericDao;
	private CustomerReviewModel review;
	private UserModel voter;
	private CustomerReviewVoteModel vote;

	private static final PK    REVIEW_PK    = PK.fromLong(123456789L);
	private static final String VOTER_UID   = "test.user@telefonica.com";

	@Before
	public void setUp() {
		service                     = new DefaultTelefonicaReviewVoteService();
		modelService                = mock(ModelService.class);
		customerReviewVoteGenericDao = mock(GenericDao.class);
		review                      = mock(CustomerReviewModel.class);
		voter                       = mock(UserModel.class);
		vote                        = mock(CustomerReviewVoteModel.class);

		service.setModelService(modelService);
		service.setCustomerReviewVoteGenericDao(customerReviewVoteGenericDao);

		when(voter.getUid()).thenReturn(VOTER_UID);
		when(review.getPk()).thenReturn(REVIEW_PK);
	}

	@Test
	public void vote_ShouldCreateAndSaveVote_WhenUserHasNotVoted() {
		when(customerReviewVoteGenericDao.find(anyMap()))
			.thenReturn(Collections.emptyList());
		when(modelService.create(CustomerReviewVoteModel.class)).thenReturn(vote);
		when(review.getUsefulCount()).thenReturn(0);

		service.vote(review, voter);

		verify(modelService).create(CustomerReviewVoteModel.class);
		verify(vote).setReview(review);
		verify(vote).setVoter(voter);
		verify(vote).setVotedAt(any());
		verify(modelService).save(vote);
	}

	@Test
	public void vote_ShouldIncrementUsefulCount_WhenUserHasNotVoted() {
		when(customerReviewVoteGenericDao.find(anyMap()))
			.thenReturn(Collections.emptyList());
		when(modelService.create(CustomerReviewVoteModel.class)).thenReturn(vote);
		when(review.getUsefulCount()).thenReturn(3);

		service.vote(review, voter);

		verify(review).setUsefulCount(4);
		verify(modelService).save(review);
	}

	@Test
	public void vote_ShouldSetUsefulCountToOne_WhenUsefulCountIsNull() {
		when(customerReviewVoteGenericDao.find(anyMap()))
			.thenReturn(Collections.emptyList());
		when(modelService.create(CustomerReviewVoteModel.class)).thenReturn(vote);
		when(review.getUsefulCount()).thenReturn(null);

		service.vote(review, voter);

		verify(review).setUsefulCount(1);
	}

	@Test
	public void vote_ShouldSaveBothVoteAndReview_WhenUserHasNotVoted() {
		when(customerReviewVoteGenericDao.find(anyMap()))
			.thenReturn(Collections.emptyList());
		when(modelService.create(CustomerReviewVoteModel.class)).thenReturn(vote);
		when(review.getUsefulCount()).thenReturn(0);

		service.vote(review, voter);

		verify(modelService, times(1)).save(vote);
		verify(modelService, times(1)).save(review);
	}

	@Test
	public void vote_ShouldThrowIllegalStateException_WhenUserHasAlreadyVoted() {
		when(customerReviewVoteGenericDao.find(anyMap()))
			.thenReturn(Collections.singletonList(vote));

		try {
			service.vote(review, voter);
			Assert.fail("Expected IllegalStateException");
		} catch (IllegalStateException e) {
			Assert.assertTrue(e.getMessage().contains(VOTER_UID));
			Assert.assertTrue(e.getMessage().contains(REVIEW_PK.toString()));
		}
	}

	@Test
	public void vote_ShouldNotCreateVote_WhenUserHasAlreadyVoted() {
		when(customerReviewVoteGenericDao.find(anyMap()))
			.thenReturn(Collections.singletonList(vote));

		try {
			service.vote(review, voter);
		} catch (IllegalStateException e) {

		}

		verify(modelService, never()).create(CustomerReviewVoteModel.class);
		verify(modelService, never()).save(any());
	}

	@Test
	public void hasVoted_ShouldReturnTrue_WhenVoteExists() {
		when(customerReviewVoteGenericDao.find(anyMap()))
			.thenReturn(Collections.singletonList(vote));

		boolean result = service.hasVoted(review, voter);

		Assert.assertTrue(result);
	}

	@Test
	public void hasVoted_ShouldReturnFalse_WhenNoVoteExists() {
		when(customerReviewVoteGenericDao.find(anyMap()))
			.thenReturn(Collections.emptyList());

		boolean result = service.hasVoted(review, voter);

		Assert.assertFalse(result);
	}

	@Test
	public void hasVoted_ShouldQueryWithCorrectParams_WhenCalled() {
		when(customerReviewVoteGenericDao.find(anyMap()))
			.thenReturn(Collections.emptyList());

		service.hasVoted(review, voter);

		verify(customerReviewVoteGenericDao).find(anyMap());
	}

	@Test
	public void getReviewByPk_ShouldReturnReview_WhenPkIsValid() {
		when(modelService.get(REVIEW_PK)).thenReturn(review);

		CustomerReviewModel result = service.getReviewByPk(String.valueOf(REVIEW_PK.getLong()));

		Assert.assertEquals(review, result);
	}

	@Test
	public void getReviewByPk_ShouldReturnNull_WhenPkIsInvalidString() {
		CustomerReviewModel result = service.getReviewByPk("invalid-pk");

		Assert.assertNull(result);
	}

	@Test
	public void getReviewByPk_ShouldReturnNull_WhenModelServiceThrowsException() {
		when(modelService.get(any(PK.class))).thenThrow(new RuntimeException("not found"));

		CustomerReviewModel result = service.getReviewByPk(String.valueOf(REVIEW_PK.getLong()));

		Assert.assertNull(result);
	}

	@Test
	public void getReviewByPk_ShouldReturnNull_WhenPkIsNull() {
		CustomerReviewModel result = service.getReviewByPk(null);

		Assert.assertNull(result);
	}

	@Test
	public void setModelService_ShouldUseNewModelService_WhenReplaced() {
		ModelService otherModelService = mock(ModelService.class);
		when(customerReviewVoteGenericDao.find(anyMap()))
			.thenReturn(Collections.emptyList());
		when(otherModelService.create(CustomerReviewVoteModel.class)).thenReturn(vote);
		when(review.getUsefulCount()).thenReturn(0);

		service.setModelService(otherModelService);
		service.vote(review, voter);

		verify(otherModelService).save(vote);
		verify(modelService, never()).save(any());
	}

	@Test
	public void setCustomerReviewVoteGenericDao_ShouldUseNewDao_WhenReplaced() {
		GenericDao<CustomerReviewVoteModel> otherDao = mock(GenericDao.class);
		when(otherDao.find(anyMap())).thenReturn(Collections.emptyList());

		service.setCustomerReviewVoteGenericDao(otherDao);
		service.hasVoted(review, voter);

		verify(otherDao).find(anyMap());
		verify(customerReviewVoteGenericDao, never()).find(anyMap());
	}
}
