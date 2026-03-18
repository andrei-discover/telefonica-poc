package br.com.telefonica.core.dao;

import br.com.telefonica.core.model.ProductQuestionModel;

import java.util.List;

public interface ProductQuestionDao
{
    List<ProductQuestionModel> findQuestionsByProductCode(String productCode);
}
