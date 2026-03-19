package br.com.telefonica.core.event;


import de.hybris.platform.servicelayer.event.ClusterAwareEvent;
import de.hybris.platform.servicelayer.event.events.AbstractEvent;
import de.hybris.platform.core.PK;

public class VerifiedPurchaseCheckEvent extends AbstractEvent implements ClusterAwareEvent {
	private static final long serialVersionUID = 1L;
	private final PK reviewPk;

	public VerifiedPurchaseCheckEvent(final PK reviewPk) {
		this.reviewPk = reviewPk;
	}

	public PK getReviewPk() {
		return reviewPk;
	}

	@Override
	public boolean publish(int sourceNodeId, int targetNodeId) {
		return true;
	}
}
