package br.com.telefonica.core.event;

import de.hybris.platform.core.PK;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class VerifiedPurchaseCheckEventTest {

	private static final PK VALID_PK = PK.fromLong(123456789L);

	private VerifiedPurchaseCheckEvent newEvent(final PK pk) {
		return new VerifiedPurchaseCheckEvent(pk);
	}

	@Nested
	class ConstructorTest {

		@Test
		void shouldCreateEventWithValidPk() {
			final VerifiedPurchaseCheckEvent event = newEvent(VALID_PK);

			assertThat(event).isNotNull();
		}

		@Test
		void shouldAcceptNullPkWithoutException() {

			final VerifiedPurchaseCheckEvent event = newEvent(null);

			assertThat(event).isNotNull();
			assertThat(event.getReviewPk()).isNull();
		}
	}

	@Nested
	class GetReviewPkTest {

		@Test
		void shouldReturnSamePkPassedToConstructor() {
			final VerifiedPurchaseCheckEvent event = newEvent(VALID_PK);

			assertThat(event.getReviewPk()).isSameAs(VALID_PK);
		}

		@Test
		void shouldReturnPkWithSameLongValue() {
			final long pkValue = 987654321L;
			final PK pk = PK.fromLong(pkValue);
			final VerifiedPurchaseCheckEvent event = newEvent(pk);

			assertThat(event.getReviewPk().getLongValueAsString())
				.isEqualTo(String.valueOf(pkValue));
		}

		@Test
		void shouldReturnNullWhenConstructedWithNull() {
			final VerifiedPurchaseCheckEvent event = newEvent(null);

			assertThat(event.getReviewPk()).isNull();
		}

		@Test
		void reviewPkShouldBeImmutableAfterConstruction() {
			final VerifiedPurchaseCheckEvent event = newEvent(VALID_PK);

			assertThat(event.getReviewPk())
				.isSameAs(event.getReviewPk())
				.isSameAs(VALID_PK);
		}
	}

	@Nested
	class PublishTest {

		@Test
		void shouldReturnTrueWhenSourceAndTargetAreEqual() {
			final VerifiedPurchaseCheckEvent event = newEvent(VALID_PK);

			assertThat(event.publish(1, 1)).isTrue();
		}

		@Test
		void shouldReturnTrueWhenSourceAndTargetAreDifferent() {
			final VerifiedPurchaseCheckEvent event = newEvent(VALID_PK);

			assertThat(event.publish(1, 2)).isTrue();
		}

		@Test
		void shouldReturnTrueForZeroNodeIds() {
			final VerifiedPurchaseCheckEvent event = newEvent(VALID_PK);

			assertThat(event.publish(0, 0)).isTrue();
		}

		@Test
		void shouldReturnTrueForNegativeNodeIds() {
			final VerifiedPurchaseCheckEvent event = newEvent(VALID_PK);

			assertThat(event.publish(-1, -99)).isTrue();
		}

		@Test
		void shouldReturnTrueForMaxIntNodeIds() {
			final VerifiedPurchaseCheckEvent event = newEvent(VALID_PK);

			assertThat(event.publish(Integer.MAX_VALUE, Integer.MAX_VALUE))
				.isTrue();
		}

		@Test
		void shouldAlwaysReturnTrueRegardlessOfNodes() {
			final VerifiedPurchaseCheckEvent event = newEvent(VALID_PK);

			assertThat(event.publish(0,   1  )).isTrue();
			assertThat(event.publish(1,   0  )).isTrue();
			assertThat(event.publish(100, 200)).isTrue();
		}
	}

	@Nested
	class ClusterAwareEventContractTest {

		@Test
		void shouldImplementClusterAwareEvent() {
			final VerifiedPurchaseCheckEvent event = newEvent(VALID_PK);

			assertThat(event)
				.isInstanceOf(de.hybris.platform.servicelayer.event.ClusterAwareEvent.class);
		}

		@Test
		void shouldExtendAbstractEvent() {
			final VerifiedPurchaseCheckEvent event = newEvent(VALID_PK);

			assertThat(event)
				.isInstanceOf(de.hybris.platform.servicelayer.event.events.AbstractEvent.class);
		}
	}

	@Nested
	@DisplayName("serialVersionUID")
	class SerialVersionUIDTest {

		@Test
		void shouldHaveCorrectSerialVersionUID() throws Exception {
			final var field = VerifiedPurchaseCheckEvent.class
				.getDeclaredField("serialVersionUID");
			field.setAccessible(true);

			assertThat(field.getLong(null)).isEqualTo(1L);
		}

		@Test
		void shouldBeSerializable() {
			assertThat(VerifiedPurchaseCheckEvent.class)
				.isAssignableFrom(java.io.Serializable.class);
		}
	}
}
