package br.com.telefonica.core.service;

import br.com.telefonica.core.model.ProductQuestionModel;

import java.util.List;

public interface ProductQuestionService
{
    ProductQuestionModel createQuestion(String productCode, String question);

    List<ProductQuestionModel> getQuestionsForProduct(String productCode);
}
