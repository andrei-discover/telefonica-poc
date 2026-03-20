package br.com.telefonica.core.event;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.PK;
import de.hybris.platform.servicelayer.event.ClusterAwareEvent;
import de.hybris.platform.servicelayer.event.events.AbstractEvent;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class VerifiedPurchaseCheckEventTest {

	private static final PK VALID_PK = PK.fromLong(123456789L);

	@Test
	public void constructor_ShouldCreateEvent_WhenValidPkIsProvided() {
		VerifiedPurchaseCheckEvent event = new VerifiedPurchaseCheckEvent(VALID_PK);

		Assert.assertNotNull(event);
	}

	@Test
	public void constructor_ShouldAcceptNullPk_WhenNullIsProvided() {
		VerifiedPurchaseCheckEvent event = new VerifiedPurchaseCheckEvent(null);

		Assert.assertNotNull(event);
		Assert.assertNull(event.getReviewPk());
	}

	@Test
	public void getReviewPk_ShouldReturnSamePk_WhenConstructedWithValidPk() {
		VerifiedPurchaseCheckEvent event = new VerifiedPurchaseCheckEvent(VALID_PK);

		Assert.assertSame(VALID_PK, event.getReviewPk());
	}

	@Test
	public void getReviewPk_ShouldReturnPkWithCorrectLongValue_WhenConstructed() {
		final long pkValue = 987654321L;
		final PK pk = PK.fromLong(pkValue);
		VerifiedPurchaseCheckEvent event = new VerifiedPurchaseCheckEvent(pk);

		Assert.assertEquals(pkValue, (long) event.getReviewPk().getLong());
	}

	@Test
	public void getReviewPk_ShouldReturnNull_WhenConstructedWithNull() {
		VerifiedPurchaseCheckEvent event = new VerifiedPurchaseCheckEvent(null);

		Assert.assertNull(event.getReviewPk());
	}

	@Test
	public void getReviewPk_ShouldReturnConsistentReference_WhenCalledMultipleTimes() {
		VerifiedPurchaseCheckEvent event = new VerifiedPurchaseCheckEvent(VALID_PK);

		Assert.assertSame(event.getReviewPk(), event.getReviewPk());
	}

	@Test
	public void publish_ShouldReturnTrue_WhenSourceAndTargetAreEqual() {
		VerifiedPurchaseCheckEvent event = new VerifiedPurchaseCheckEvent(VALID_PK);

		Assert.assertTrue(event.publish(1, 1));
	}

	@Test
	public void publish_ShouldReturnTrue_WhenSourceAndTargetAreDifferent() {
		VerifiedPurchaseCheckEvent event = new VerifiedPurchaseCheckEvent(VALID_PK);

		Assert.assertTrue(event.publish(1, 2));
	}

	@Test
	public void publish_ShouldReturnTrue_WhenNodeIdsAreZero() {
		VerifiedPurchaseCheckEvent event = new VerifiedPurchaseCheckEvent(VALID_PK);

		Assert.assertTrue(event.publish(0, 0));
	}

	@Test
	public void publish_ShouldReturnTrue_WhenNodeIdsAreNegative() {
		VerifiedPurchaseCheckEvent event = new VerifiedPurchaseCheckEvent(VALID_PK);

		Assert.assertTrue(event.publish(-1, -99));
	}

	@Test
	public void publish_ShouldReturnTrue_WhenNodeIdsAreMaxInt() {
		VerifiedPurchaseCheckEvent event = new VerifiedPurchaseCheckEvent(VALID_PK);

		Assert.assertTrue(event.publish(Integer.MAX_VALUE, Integer.MAX_VALUE));
	}

	@Test
	public void publish_ShouldAlwaysReturnTrue_ForAnyNodeCombination() {
		VerifiedPurchaseCheckEvent event = new VerifiedPurchaseCheckEvent(VALID_PK);

		Assert.assertTrue(event.publish(0,   1  ));
		Assert.assertTrue(event.publish(1,   0  ));
		Assert.assertTrue(event.publish(100, 200));
	}

	@Test
	public void event_ShouldImplementClusterAwareEvent() {
		VerifiedPurchaseCheckEvent event = new VerifiedPurchaseCheckEvent(VALID_PK);

		Assert.assertTrue(event instanceof ClusterAwareEvent);
	}

	@Test
	public void event_ShouldExtendAbstractEvent() {
		VerifiedPurchaseCheckEvent event = new VerifiedPurchaseCheckEvent(VALID_PK);

		Assert.assertTrue(event instanceof AbstractEvent);
	}

	@Test
	public void event_ShouldBeSerializable() {
		Assert.assertTrue(
			java.io.Serializable.class.isAssignableFrom(VerifiedPurchaseCheckEvent.class));
	}

	@Test
	public void event_ShouldHaveCorrectSerialVersionUID() throws Exception {
		final java.lang.reflect.Field field =
			VerifiedPurchaseCheckEvent.class.getDeclaredField("serialVersionUID");
		field.setAccessible(true);

		Assert.assertEquals(1L, field.getLong(null));
	}
}
