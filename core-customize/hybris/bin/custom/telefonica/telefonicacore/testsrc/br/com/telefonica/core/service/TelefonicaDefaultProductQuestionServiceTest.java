package br.com.telefonica.core.service;

import br.com.telefonica.core.dao.ProductQuestionDao;
import br.com.telefonica.core.enums.QuestionStatus;
import br.com.telefonica.core.model.ProductQuestionModel;
import br.com.telefonica.core.service.impl.DefaultProductQuestionService;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TelefonicaDefaultProductQuestionServiceTest {

    private DefaultProductQuestionService service;
    private ModelService modelService;
    private ProductService productService;
    private UserService userService;
    private ProductQuestionDao productQuestionDao;

    @BeforeEach
    void setup() {
        modelService = mock(ModelService.class);
        productService = mock(ProductService.class);
        userService = mock(UserService.class);
        productQuestionDao = mock(ProductQuestionDao.class);

        service = new DefaultProductQuestionService();
        service.setModelService(modelService);
        service.setProductService(productService);
        service.setUserService(userService);
        service.setProductQuestionDao(productQuestionDao);
    }

    @Test
    @DisplayName("Deve criar pergunta PENDING e salvar corretamente")
    void createQuestion_success_setsFieldsAndSaves() {
        final String productCode = "iPhone_13_blue_128g";
        final String questionText = "Esta é uma pergunta de teste";

        CustomerModel customer = new CustomerModel();
        when(userService.getCurrentUser()).thenReturn(customer);

        ProductModel product = new ProductModel();
        when(productService.getProductForCode(productCode)).thenReturn(product);

        ProductQuestionModel created = mock(ProductQuestionModel.class);
        when(modelService.create(ProductQuestionModel.class)).thenReturn(created);

        ProductQuestionModel result = service.createQuestion(productCode, questionText);

        assertSame(created, result);
        verify(created).setQuestion(questionText);
        verify(created).setStatus(QuestionStatus.PENDING);
        verify(created).setCustomer(customer);
        verify(created).setProduct(product);
        verify(modelService).save(created);
    }

    @Test
    @DisplayName("Deve lançar exceção quando pergunta em branco")
    void createQuestion_blankQuestion_throws() {
        when(userService.getCurrentUser()).thenReturn(new CustomerModel());
        assertThrows(IllegalArgumentException.class,
                () -> service.createQuestion("code", "   "));
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não é Customer")
    void createQuestion_userNotCustomer_throws() {
        when(userService.getCurrentUser()).thenReturn(new UserModel());
        assertThrows(IllegalStateException.class,
                () -> service.createQuestion("code", "ok?"));
    }

    @Test
    @DisplayName("Deve delegar busca de perguntas para DAO")
    void getQuestionsForProduct_delegatesToDao() {
        List<ProductQuestionModel> expected = Collections.singletonList(new ProductQuestionModel());
        when(productQuestionDao.findQuestionsByProductCode("code")).thenReturn(expected);

        List<ProductQuestionModel> actual = service.getQuestionsForProduct("code");

        assertSame(expected, actual);
        verify(productQuestionDao).findQuestionsByProductCode("code");
    }
}