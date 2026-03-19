package br.com.telefonica.core.service.impl;

import br.com.telefonica.core.dao.TelefonicaProductQuestionDao;
import br.com.telefonica.core.enums.QuestionStatus;
import br.com.telefonica.core.model.ProductQuestionModel;
import br.com.telefonica.core.service.TelefonicaProductQuestionService;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.List;

public class TelefonicaDefaultProductQuestionService implements TelefonicaProductQuestionService
{

    private ModelService modelService;
    private ProductService productService;
    private UserService userService;
    private TelefonicaProductQuestionDao productQuestionDao;

    @Override
    public ProductQuestionModel createQuestion(String productCode, String questionText) {

        if (questionText == null || questionText.trim().isEmpty()) {
            throw new IllegalArgumentException("Question must not be empty");
        }

        final UserModel user = userService.getCurrentUser();
        if (!(user instanceof CustomerModel)) {
            throw new IllegalStateException("User must be a customer");
        }

        final ProductModel product = productService.getProductForCode(productCode);
        final ProductQuestionModel questionModel = modelService.create(ProductQuestionModel.class);

        questionModel.setQuestion(questionText);
        questionModel.setStatus(QuestionStatus.PENDING);
        questionModel.setCustomer((CustomerModel) user);
        questionModel.setProduct(product);

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

    public void setProductQuestionDao(final TelefonicaProductQuestionDao productQuestionDao)
    {
        this.productQuestionDao = productQuestionDao;
    }
}
