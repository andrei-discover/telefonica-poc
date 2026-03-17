package br.com.telefonica.facades.productquestion.populator;

import br.com.telefonica.core.model.ProductQuestionModel;
import br.com.telefonica.facades.productquestion.dto.ProductQuestionResponseDTO;
import de.hybris.platform.converters.Populator;

public class ProductQuestionPopulator implements Populator<ProductQuestionModel, ProductQuestionResponseDTO>
{

    @Override
    public void populate(ProductQuestionModel source, ProductQuestionResponseDTO target)
    {
        if (source.getProduct() != null)
        {
            target.setProductCode(source.getProduct().getCode());
        }

        target.setQuestion(source.getQuestion());

        if (source.getStatus() != null)
        {
            target.setStatus(source.getStatus().getCode());
        }

        if (source.getCustomer() != null)
        {
            target.setAuthor(source.getCustomer().getUid());
        }

        if (source.getCreationtime() != null)
        {
            target.setCreatedDate(source.getCreationtime()
                    .toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDateTime());
        }
    }
}