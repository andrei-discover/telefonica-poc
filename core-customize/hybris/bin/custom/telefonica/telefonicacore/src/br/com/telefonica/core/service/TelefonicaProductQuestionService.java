package br.com.telefonica.core.service;

import br.com.telefonica.core.model.ProductQuestionModel;

import java.util.List;

public interface TelefonicaProductQuestionService
{
    ProductQuestionModel createQuestion(String productCode, String question);
    List<ProductQuestionModel> getApprovedQuestionsForProduct(final String productCode);
}
