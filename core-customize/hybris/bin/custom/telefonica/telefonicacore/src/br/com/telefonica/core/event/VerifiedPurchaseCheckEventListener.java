package br.com.telefonica.core.event;

import br.com.telefonica.core.dao.TelefonicaCustomerReviewDao;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.customerreview.model.CustomerReviewModel;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.log4j.Logger;

import java.util.Objects;

public class VerifiedPurchaseCheckEventListener extends AbstractEventListener<VerifiedPurchaseCheckEvent> {
	private ModelService modelService;
	private TelefonicaCustomerReviewDao telefonicaCustomerReviewDao;

	private static final Logger LOG = Logger.getLogger(VerifiedPurchaseCheckEventListener.class);

	@Override
	protected void onEvent(final VerifiedPurchaseCheckEvent event) {
		LOG.info("[VerifiedPurchaseCheckEventListener] - Starting event listener");

		final PK reviewPk = event.getReviewPk();

		final CustomerReviewModel review = modelService.get(reviewPk);

		if (Objects.nonNull(review)) {
			final UserModel user = review.getUser();
			final ProductModel product = review.getProduct();

			boolean verified = isVerifiedPurchase(user, product);

			review.setVerifiedPurchase(verified);
			modelService.save(review);
		}

		LOG.info("[VerifiedPurchaseCheckEventListener] - Finishing event listener");
	}

	protected boolean isVerifiedPurchase(final UserModel user, final ProductModel product) {

		LOG.info("[VerifiedPurchaseCheckEventListener] - Start purchase verification");
		if (!(user instanceof CustomerModel)) {
			return false;
		}

		final CustomerModel customer = (CustomerModel) user;

		final boolean verified = telefonicaCustomerReviewDao
			.hasCustomerPurchasedProduct(customer, product);

		LOG.info("[VerifiedPurchaseCheckEventListener] - verified purchase = " + verified);
		return verified;
	}

	public void setTelefonicaCustomerReviewDao(TelefonicaCustomerReviewDao telefonicaCustomerReviewDao)	{
		this.telefonicaCustomerReviewDao = telefonicaCustomerReviewDao;
	}

	public void setModelService(ModelService modelService)	{
		this.modelService = modelService;
	}
}
