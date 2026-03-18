package br.com.telefonica.facades.productquestion;

import br.com.telefonica.facades.productquestion.data.ProductQuestionData;
import br.com.telefonica.facades.productquestion.dto.ProductQuestionResponseDTO;

import java.util.List;

public interface ProductQuestionFacade
{
    ProductQuestionData createQuestion(String baseSiteId, ProductQuestionData requestDTO);
    public List<ProductQuestionData> getQuestionsForProduct(String baseSiteId, String productCode);
}
