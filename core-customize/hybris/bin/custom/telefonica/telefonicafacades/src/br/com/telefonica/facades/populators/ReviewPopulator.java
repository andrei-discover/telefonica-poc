package br.com.telefonica.facades.populators;

import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.product.data.ReviewData;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.customerreview.model.CustomerReviewModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import de.hybris.platform.servicelayer.dto.converter.Converter;
import java.util.Objects;

public class ReviewPopulator implements Populator<CustomerReviewModel, ReviewData> {
	private Converter<ProductModel, ProductData> productConverter;

	@Override
	public void populate(final CustomerReviewModel source, final ReviewData target)
		throws ConversionException {

		Objects.requireNonNull(source, "source must not be null");
		Objects.requireNonNull(target, "target must not be null");

		if (source.getProduct() != null) {
			target.setProduct(productConverter.convert(source.getProduct()));
		}

		target.setRating(source.getRating());
		target.setHeadline(source.getHeadline());
		target.setComment(source.getComment());
		target.setReviewPk(source.getPk().toString());

		if (source.getVerifiedPurchase() != null) {
			target.setVerifiedPurchase(source.getVerifiedPurchase());
		}

		if (source.getApprovalStatus() != null) {
			target.setApprovalStatus(source.getApprovalStatus().getCode());
		}
	}

	public void setProductConverter(
		final Converter<ProductModel, ProductData> productConverter) {
		this.productConverter = productConverter;
	}

}
