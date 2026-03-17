package br.com.telefonica.core.dao;

import br.com.telefonica.core.model.ProductQuestionModel;

import java.util.List;

public interface ProductQuestionDao
{
    /**
     * Busca todas as perguntas associadas a um produto.
     *
     * @param productCode código do produto
     * @return lista de perguntas ordenadas por data de criação
     */
    List<ProductQuestionModel> findQuestionsByProductCode(String productCode);
}
