package br.com.telefonica.core.service;

import br.com.telefonica.core.dao.TelefonicaProductQuestionDao;
import br.com.telefonica.core.enums.QuestionStatus;
import br.com.telefonica.core.model.ProductQuestionModel;
import br.com.telefonica.core.service.impl.TelefonicaDefaultProductQuestionService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class TelefonicaDefaultProductQuestionServiceTest {

    @Mock
    private ModelService modelService;

    @Mock
    private ProductService productService;

    @Mock
    private UserService userService;

    @Mock
    private TelefonicaProductQuestionDao productQuestionDao;

    @InjectMocks
    private TelefonicaDefaultProductQuestionService service;

    @Before
    public void setUp() {
    }

    @Test
    public void createQuestion_success_setsFieldsAndSaves() {
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
    public void createQuestion_blankQuestion_throws() {
        when(userService.getCurrentUser()).thenReturn(new CustomerModel());

        try {
            service.createQuestion("code", "   ");
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // ok
        }
    }

    @Test
    public void createQuestion_userNotCustomer_throws() {
        when(userService.getCurrentUser()).thenReturn(new UserModel());

        try {
            service.createQuestion("code", "ok?");
            fail("Expected IllegalStateException");
        } catch (IllegalStateException e) {
        }
    }

    @Test
    public void getApprovedQuestionsForProduct_delegatesToDao() {

        final String productCode = "code";

        ProductModel product = new ProductModel();
        List<ProductQuestionModel> expected = Collections.singletonList(new ProductQuestionModel());

        when(productService.getProductForCode(productCode)).thenReturn(product);

        when(productQuestionDao.findQuestionsByProductCodeAndStatus(product, QuestionStatus.APPROVED))
                .thenReturn(expected);

        List<ProductQuestionModel> actual = service.getApprovedQuestionsForProduct(productCode);

        assertSame(expected, actual);

        // verifica chamadas corretas
        verify(productService).getProductForCode(productCode);
        verify(productQuestionDao).findQuestionsByProductCodeAndStatus(product, QuestionStatus.APPROVED);
    }
}