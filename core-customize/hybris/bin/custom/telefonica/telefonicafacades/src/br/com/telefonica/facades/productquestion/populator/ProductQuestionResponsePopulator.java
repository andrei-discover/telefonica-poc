package br.com.telefonica.facades.productquestion.populator;

import br.com.telefonica.facades.productquestion.data.ProductQuestionData;
import br.com.telefonica.facades.productquestion.dto.ProductQuestionResponseDTO;
import de.hybris.platform.converters.Populator;

public class ProductQuestionResponsePopulator implements Populator<ProductQuestionData, ProductQuestionResponseDTO> {

    @Override
    public void populate(ProductQuestionData source, ProductQuestionResponseDTO target) {
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