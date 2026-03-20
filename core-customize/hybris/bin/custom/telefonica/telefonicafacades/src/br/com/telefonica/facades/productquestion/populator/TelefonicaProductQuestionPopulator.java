package br.com.telefonica.facades.productquestion.populator;

import br.com.telefonica.core.model.ProductQuestionModel;
import br.com.telefonica.facades.productquestion.data.ProductQuestionData;
import de.hybris.platform.converters.Populator;

import java.util.Objects;

public class TelefonicaProductQuestionPopulator
        implements Populator<ProductQuestionModel, ProductQuestionData> {

    @Override
    public void populate(ProductQuestionModel source, ProductQuestionData target) {

        Objects.requireNonNull(source, "Parameter source cannot be null");
        Objects.requireNonNull(target, "Parameter target cannot be null");


        if (source.getProduct() != null) {
            target.setProductCode(source.getProduct().getCode());
        }

        target.setQuestion(source.getQuestion());
        target.setAnswer(source.getAnswer());

        if (source.getStatus() != null) {
            target.setStatus(source.getStatus().name());
        }

        if (source.getCustomer() != null) {
            target.setAuthor(source.getCustomer().getUid());
        }

        target.setCreatedDate(source.getCreationtime());
    }
}