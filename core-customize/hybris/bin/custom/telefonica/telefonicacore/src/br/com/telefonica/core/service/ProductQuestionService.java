package br.com.telefonica.core.service;

import br.com.telefonica.core.model.ProductQuestionModel;

import java.util.List;

public interface ProductQuestionService
{
    /**
     * Cria uma nova pergunta associada a um produto.
     *
     * @param productCode código do produto
     * @param question texto da pergunta
     * @return ProductQuestionModel persistido
     */
    ProductQuestionModel createQuestion(String productCode, String question);

    /**
     * Retorna todas as perguntas associadas a um produto.
     *
     * @param productCode código do produto
     * @return lista de perguntas
     */
    List<ProductQuestionModel> getQuestionsForProduct(String productCode);
}
