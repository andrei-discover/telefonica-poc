package br.com.telefonica.facades.populators;

import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.product.data.ReviewData;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.customerreview.enums.CustomerReviewApprovalType;
import de.hybris.platform.customerreview.model.CustomerReviewModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class ReviewPopulatorTest {

	private static final long REVIEW_PK_VALUE = 123456789L;
	private static final PK   REVIEW_PK       = PK.fromLong(REVIEW_PK_VALUE);

	@InjectMocks
	private ReviewPopulator populator;

	@Mock
	private Converter<ProductModel, ProductData> productConverter;

	@Mock
	private CustomerReviewModel source;

	private ReviewData target;

	@BeforeEach
	void setUp() {
		target = new ReviewData();
		// getPk() é chamado em toda execução do populate — stub padrão aqui
		given(source.getPk()).willReturn(REVIEW_PK);
	}

	@Nested
	class NullGuardTest {

		@Test
		void shouldThrowNpeWhenSourceIsNull() {
			assertThatThrownBy(() -> populator.populate(null, target))
				.isInstanceOf(NullPointerException.class)
				.hasMessage("source must not be null");
		}

		@Test
		void shouldThrowNpeWhenTargetIsNull() {
			assertThatThrownBy(() -> populator.populate(source, null))
				.isInstanceOf(NullPointerException.class)
				.hasMessage("target must not be null");
		}
	}

	@Nested
	class ReviewPkTest {

		@Test
		void shouldSetReviewPkWithLongValueFromSourcePk() {
			populator.populate(source, target);

			assertThat(target.getReviewPk()).isEqualTo(REVIEW_PK_VALUE);
		}

		@Test
		void shouldSetDifferentReviewPkForDifferentSources() {
			final long otherPkValue = 987654321L;
			given(source.getPk()).willReturn(PK.fromLong(otherPkValue));

			populator.populate(source, target);

			assertThat(target.getReviewPk()).isEqualTo(otherPkValue);
		}
	}

	@Nested
	class ProductTest {

		@Test
		void shouldConvertAndSetProductWhenNotNull() {
			final ProductModel productModel = new ProductModel();
			final ProductData productData = new ProductData();
			productData.setCode("PROD001");

			given(source.getProduct()).willReturn(productModel);
			given(productConverter.convert(productModel)).willReturn(productData);

			populator.populate(source, target);

			assertThat(target.getProduct()).isSameAs(productData);
			assertThat(target.getProduct().getCode()).isEqualTo("PROD001");
		}

		@Test
		void shouldNotSetProductWhenNull() {
			given(source.getProduct()).willReturn(null);

			populator.populate(source, target);

			assertThat(target.getProduct()).isNull();
			then(productConverter).should(never()).convert(any());
		}

		@Test
		void shouldDelegateConversionToConverter() {
			final ProductModel productModel = new ProductModel();
			final ProductData productData = new ProductData();

			given(source.getProduct()).willReturn(productModel);
			given(productConverter.convert(productModel)).willReturn(productData);

			populator.populate(source, target);

			then(productConverter).should().convert(productModel);
		}
	}

	@Nested
	class SimpleFieldsTest {

		@Test
		void shouldPopulateRating() {
			given(source.getRating()).willReturn(4.5);

			populator.populate(source, target);

			assertThat(target.getRating()).isEqualTo(4.5);
		}

		@Test
		void shouldPopulateZeroRating() {
			given(source.getRating()).willReturn(0.0);

			populator.populate(source, target);

			assertThat(target.getRating()).isEqualTo(0.0);
		}

		@Test
		void shouldPopulateHeadline() {
			given(source.getHeadline()).willReturn("Ótimo produto");

			populator.populate(source, target);

			assertThat(target.getHeadline()).isEqualTo("Ótimo produto");
		}

		@Test
		void shouldPopulateComment() {
			given(source.getComment()).willReturn("Recomendo muito!");

			populator.populate(source, target);

			assertThat(target.getComment()).isEqualTo("Recomendo muito!");
		}

		@Test
		void shouldAcceptNullHeadline() {
			given(source.getHeadline()).willReturn(null);

			populator.populate(source, target);

			assertThat(target.getHeadline()).isNull();
		}

		@Test
		void shouldAcceptNullComment() {
			given(source.getComment()).willReturn(null);

			populator.populate(source, target);

			assertThat(target.getComment()).isNull();
		}

		@Test
		void shouldAcceptNullRating() {
			given(source.getRating()).willReturn(null);

			populator.populate(source, target);

			assertThat(target.getRating()).isNull();
		}
	}

	@Nested
	class VerifiedPurchaseTest {

		@Test
		void shouldSetVerifiedPurchaseTrueWhenTrue() {
			given(source.getVerifiedPurchase()).willReturn(Boolean.TRUE);

			populator.populate(source, target);

			assertThat(target.getVerifiedPurchase()).isTrue();
		}

		@Test
		void shouldSetVerifiedPurchaseFalseWhenFalse() {
			given(source.getVerifiedPurchase()).willReturn(Boolean.FALSE);

			populator.populate(source, target);

			assertThat(target.getVerifiedPurchase()).isFalse();
		}

		@Test
		void shouldNotSetVerifiedPurchaseWhenNull() {
			given(source.getVerifiedPurchase()).willReturn(null);

			populator.populate(source, target);

			assertThat(target.getVerifiedPurchase()).isNull();
		}
	}

	@Nested
	class ApprovalStatusTest {

		@Test
		void shouldSetApprovalStatusCodeWhenApproved() {
			given(source.getApprovalStatus()).willReturn(CustomerReviewApprovalType.APPROVED);

			populator.populate(source, target);

			assertThat(target.getApprovalStatus())
				.isEqualTo(CustomerReviewApprovalType.APPROVED.getCode());
		}

		@Test
		void shouldSetApprovalStatusCodeWhenPending() {
			given(source.getApprovalStatus()).willReturn(CustomerReviewApprovalType.PENDING);

			populator.populate(source, target);

			assertThat(target.getApprovalStatus())
				.isEqualTo(CustomerReviewApprovalType.PENDING.getCode());
		}

		@Test
		void shouldSetApprovalStatusCodeWhenRejected() {
			given(source.getApprovalStatus()).willReturn(CustomerReviewApprovalType.REJECTED);

			populator.populate(source, target);

			assertThat(target.getApprovalStatus())
				.isEqualTo(CustomerReviewApprovalType.REJECTED.getCode());
		}

		@Test
		void shouldNotSetApprovalStatusWhenNull() {
			given(source.getApprovalStatus()).willReturn(null);

			populator.populate(source, target);

			assertThat(target.getApprovalStatus()).isNull();
		}
	}

	@Nested
	class FullScenarioTest {

		@Test
		void shouldPopulateAllFieldsWhenSourceIsComplete() {
			final ProductModel productModel = new ProductModel();
			final ProductData productData = new ProductData();
			productData.setCode("PROD001");

			given(source.getProduct()).willReturn(productModel);
			given(productConverter.convert(productModel)).willReturn(productData);
			given(source.getRating()).willReturn(5.0);
			given(source.getHeadline()).willReturn("Excelente");
			given(source.getComment()).willReturn("Produto de alta qualidade.");
			given(source.getVerifiedPurchase()).willReturn(Boolean.TRUE);
			given(source.getApprovalStatus()).willReturn(CustomerReviewApprovalType.APPROVED);

			populator.populate(source, target);

			assertThat(target.getProduct()).isSameAs(productData);
			assertThat(target.getReviewPk()).isEqualTo(REVIEW_PK_VALUE);
			assertThat(target.getRating()).isEqualTo(5.0);
			assertThat(target.getHeadline()).isEqualTo("Excelente");
			assertThat(target.getComment()).isEqualTo("Produto de alta qualidade.");
			assertThat(target.getVerifiedPurchase()).isTrue();
			assertThat(target.getApprovalStatus())
				.isEqualTo(CustomerReviewApprovalType.APPROVED.getCode());
		}

		@Test
		void shouldPopulateMandatoryFieldsWhenOptionalsAreNull() {
			given(source.getProduct()).willReturn(null);
			given(source.getRating()).willReturn(3.0);
			given(source.getHeadline()).willReturn("Ok");
			given(source.getComment()).willReturn(null);
			given(source.getVerifiedPurchase()).willReturn(null);
			given(source.getApprovalStatus()).willReturn(null);

			populator.populate(source, target);

			assertThat(target.getProduct()).isNull();
			assertThat(target.getReviewPk()).isEqualTo(REVIEW_PK_VALUE);
			assertThat(target.getRating()).isEqualTo(3.0);
			assertThat(target.getHeadline()).isEqualTo("Ok");
			assertThat(target.getComment()).isNull();
			assertThat(target.getVerifiedPurchase()).isNull();
			assertThat(target.getApprovalStatus()).isNull();
		}
	}

	@Nested
	class SetProductConverterTest {

		@Test
		void shouldUseInjectedConverter() {
			final ProductModel productModel = new ProductModel();
			final ProductData expectedData = new ProductData();
			expectedData.setCode("INJECTED");

			given(source.getProduct()).willReturn(productModel);
			given(productConverter.convert(productModel)).willReturn(expectedData);

			populator.populate(source, target);

			assertThat(target.getProduct().getCode()).isEqualTo("INJECTED");
		}
	}
}
