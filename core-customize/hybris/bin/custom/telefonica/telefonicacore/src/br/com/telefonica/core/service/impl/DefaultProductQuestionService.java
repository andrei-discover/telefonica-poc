package br.com.telefonica.core.service.impl;

import br.com.telefonica.core.dao.ProductQuestionDao;
import br.com.telefonica.core.enums.QuestionStatus;
import br.com.telefonica.core.model.ProductQuestionModel;
import br.com.telefonica.core.service.ProductQuestionService;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.List;

public class DefaultProductQuestionService implements ProductQuestionService
{

    private ModelService modelService;
    private ProductService productService;
    private UserService userService;
    private ProductQuestionDao productQuestionDao;

    @Override
    public ProductQuestionModel createQuestion(final String productCode, final String question) {

        if (question == null || question.trim().isEmpty()) {
            throw new IllegalArgumentException("A pergunta não pode estar vazia.");
        }

        final UserModel currentUser = userService.getCurrentUser();
        if (!(currentUser instanceof CustomerModel)) {
            throw new IllegalStateException("Usuário atual não é um cliente autenticado.");
        }

        final CustomerModel customer = (CustomerModel) currentUser;
        final ProductQuestionModel questionModel = modelService.create(ProductQuestionModel.class);
        final ProductModel product = productService.getProductForCode(productCode);

        questionModel.setProduct(product);
        questionModel.setCustomer(customer);
        questionModel.setQuestion(question);
        questionModel.setStatus(QuestionStatus.PENDING);

        modelService.save(questionModel);

        return questionModel;
    }

    @Override
    public List<ProductQuestionModel> getQuestionsForProduct(final String productCode)
    {
        return productQuestionDao.findQuestionsByProductCode(productCode);
    }

    public void setModelService(final ModelService modelService)
    {
        this.modelService = modelService;
    }

    public void setProductService(final ProductService productService)
    {
        this.productService = productService;
    }

    public void setUserService(final UserService userService)
    {
        this.userService = userService;
    }

    public void setProductQuestionDao(final ProductQuestionDao productQuestionDao)
    {
        this.productQuestionDao = productQuestionDao;
    }
}
