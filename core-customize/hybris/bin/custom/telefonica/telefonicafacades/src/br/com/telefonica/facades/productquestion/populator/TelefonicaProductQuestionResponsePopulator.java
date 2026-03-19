package br.com.telefonica.facades.productquestion.populator;

import br.com.telefonica.facades.productquestion.data.ProductQuestionData;
import br.com.telefonica.facades.productquestion.dto.ProductQuestionResponseWsDTO;
import de.hybris.platform.converters.Populator;

public class TelefonicaProductQuestionResponsePopulator implements Populator<ProductQuestionData, ProductQuestionResponseWsDTO> {

    @Override
    public void populate(ProductQuestionData source, ProductQuestionResponseWsDTO target) {
        if (source == null || target == null) {
            return;
        }

        target.setProductCode(source.getProductCode());
        target.setQuestion(source.getQuestion());
        target.setStatus(source.getStatus());
        target.setAuthor(source.getAuthor());
        target.setCreatedDate(source.getCreatedDate());
    }
}