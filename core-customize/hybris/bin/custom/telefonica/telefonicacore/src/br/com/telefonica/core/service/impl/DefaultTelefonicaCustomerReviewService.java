package br.com.telefonica.core.service.impl;

import br.com.telefonica.core.event.VerifiedPurchaseCheckEvent;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.customerreview.enums.CustomerReviewApprovalType;
import de.hybris.platform.customerreview.impl.DefaultCustomerReviewService;
import de.hybris.platform.customerreview.model.CustomerReviewModel;
import de.hybris.platform.servicelayer.event.EventService;
import br.com.telefonica.core.service.TelefonicaCustomerReviewService;
import org.apache.log4j.Logger;



public class DefaultTelefonicaCustomerReviewService extends DefaultCustomerReviewService implements TelefonicaCustomerReviewService {
	private static final Logger LOG = Logger.getLogger(DefaultTelefonicaCustomerReviewService.class);

	private EventService eventService;

	@Override
	public CustomerReviewModel createCustomerReview(Double rating, String headline, String comment, UserModel user, ProductModel product) {

		LOG.info("[DefaultTelefonicaCustomerReviewService] - Staring Customer Review Service");

		CustomerReviewModel review = super.createCustomerReview(rating, headline, comment, user, product);

		review.setApprovalStatus(CustomerReviewApprovalType.PENDING);
		review.setVerifiedPurchase(false);

		getModelService().save(review);

		LOG.info("[DefaultTelefonicaCustomerReviewService] - Published Customer Review Event");

		eventService.publishEvent(new VerifiedPurchaseCheckEvent(review.getPk()));

		LOG.info("[DefaultTelefonicaCustomerReviewService] - Returning Customer Review");
		return review;
	}

	public void setEventService(EventService eventService)	{
		this.eventService = eventService;
	}
}
