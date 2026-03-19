package br.com.telefonica.facades.productquestion.populator;

import br.com.telefonica.core.model.ProductQuestionModel;
import br.com.telefonica.facades.productquestion.data.ProductQuestionData;
import de.hybris.platform.converters.Populator;

public class TelefonicaProductQuestionPopulator
        implements Populator<ProductQuestionModel, ProductQuestionData> {

    @Override
    public void populate(ProductQuestionModel source, ProductQuestionData target) {

        target.setProductCode(source.getProduct().getCode());
        target.setQuestion(source.getQuestion());
        target.setStatus(source.getStatus().name());
        target.setAuthor(source.getCustomer().getUid());
        target.setCreatedDate(source.getCreationtime());
    }
}