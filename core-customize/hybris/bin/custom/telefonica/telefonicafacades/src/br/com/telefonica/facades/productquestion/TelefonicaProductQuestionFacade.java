package br.com.telefonica.facades.productquestion;

import br.com.telefonica.facades.productquestion.data.ProductQuestionData;

import java.util.List;

public interface TelefonicaProductQuestionFacade
{
    ProductQuestionData createQuestion(ProductQuestionData requestDTO);
    List<ProductQuestionData> getApprovedQuestionsForProduct(String productCode);
}
