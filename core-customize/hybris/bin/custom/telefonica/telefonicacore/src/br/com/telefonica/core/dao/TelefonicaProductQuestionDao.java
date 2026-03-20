package br.com.telefonica.core.dao;

import br.com.telefonica.core.enums.QuestionStatus;
import br.com.telefonica.core.model.ProductQuestionModel;
import de.hybris.platform.core.model.product.ProductModel;

import java.util.List;

public interface TelefonicaProductQuestionDao
{
    List<ProductQuestionModel> findQuestionsByProductCodeAndStatus(ProductModel product, QuestionStatus status);
}
