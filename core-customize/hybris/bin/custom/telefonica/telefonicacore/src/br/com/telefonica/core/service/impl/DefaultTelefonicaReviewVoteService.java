package br.com.telefonica.core.service.impl;

import br.com.telefonica.core.model.CustomerReviewVoteModel;
import br.com.telefonica.core.service.TelefonicaReviewVoteService;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.customerreview.model.CustomerReviewModel;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import de.hybris.platform.core.PK;


public class DefaultTelefonicaReviewVoteService implements TelefonicaReviewVoteService
{
	private static final Logger LOG = LoggerFactory.getLogger(DefaultTelefonicaReviewVoteService.class);

	private ModelService modelService;
	private GenericDao<CustomerReviewVoteModel> customerReviewVoteGenericDao;

	@Override
	public void vote(final CustomerReviewModel review, final UserModel voter)
	{
		if (hasVoted(review, voter))
		{
			throw new IllegalStateException(String.format("User %s already voted on review %s", voter.getUid(), review.getPk()));
		}

		final CustomerReviewVoteModel vote = modelService.create(CustomerReviewVoteModel.class);
		vote.setReview(review);
		vote.setVoter(voter);
		vote.setVotedAt(new Date());
		modelService.save(vote);

		final int currentVotes = review.getUsefulCount() != null ? review.getUsefulCount() : 0;
		review.setUsefulCount(currentVotes + 1);
		modelService.save(review);

		LOG.info("Vote registered — review={} voter={} totalVotes={}", review.getPk(), voter.getUid(), review.getUsefulCount());
	}

	@Override
	public boolean hasVoted(final CustomerReviewModel review, final UserModel voter)
	{
		final HashMap<String, Object> params = new HashMap<>();
		params.put(CustomerReviewVoteModel.REVIEW, review);
		params.put(CustomerReviewVoteModel.VOTER, voter);

		final List<CustomerReviewVoteModel> votes = customerReviewVoteGenericDao.find(params);
		return CollectionUtils.isNotEmpty(votes);
	}

	public CustomerReviewModel getReviewByPk(final String reviewPk) {
		try	{
			return modelService.get(PK.fromLong(Long.parseLong(reviewPk)));
		} catch (final Exception e)	{
			return null;
		}
	}

	public void setModelService(ModelService modelService)
	{
		this.modelService = modelService;
	}

	public void setCustomerReviewVoteGenericDao(GenericDao<CustomerReviewVoteModel> customerReviewVoteGenericDao)
	{
		this.customerReviewVoteGenericDao = customerReviewVoteGenericDao;
	}

}

