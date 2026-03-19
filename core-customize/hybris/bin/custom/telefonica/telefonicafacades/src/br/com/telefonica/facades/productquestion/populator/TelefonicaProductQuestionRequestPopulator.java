package br.com.telefonica.facades.productquestion.populator;

import br.com.telefonica.facades.productquestion.data.ProductQuestionData;
import br.com.telefonica.facades.productquestion.dto.ProductQuestionRequestWsDTO;
import de.hybris.platform.converters.Populator;

public class TelefonicaProductQuestionRequestPopulator implements Populator<ProductQuestionRequestWsDTO, ProductQuestionData> {

    @Override
    public void populate(ProductQuestionRequestWsDTO source, ProductQuestionData target) {
        if (source == null || target == null) {
            return;
        }

        target.setProductCode(source.getProductCode());
        target.setQuestion(source.getQuestion());
    }
}