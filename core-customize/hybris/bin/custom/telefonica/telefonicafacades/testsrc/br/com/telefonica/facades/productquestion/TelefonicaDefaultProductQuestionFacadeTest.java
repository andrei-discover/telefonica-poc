package br.com.telefonica.facades.productquestion;

import br.com.telefonica.core.model.ProductQuestionModel;
import br.com.telefonica.core.service.TelefonicaProductQuestionService;
import br.com.telefonica.facades.productquestion.data.ProductQuestionData;
import br.com.telefonica.facades.productquestion.impl.TelefonicaDefaultProductQuestionFacade;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class TelefonicaDefaultProductQuestionFacadeTest {

    @Mock
    private TelefonicaProductQuestionService service;

    @Mock
    private Converter<ProductQuestionModel, ProductQuestionData> converter;

    @InjectMocks
    private TelefonicaDefaultProductQuestionFacade facade;

    @Before
    public void setUp() {
    }

    @Test
    public void createQuestion_delegatesToServiceAndConverter() {
        ProductQuestionData requestData = new ProductQuestionData();
        requestData.setProductCode("p1");
        requestData.setQuestion("q?");

        ProductQuestionModel model = new ProductQuestionModel();

        ProductQuestionData responseData = new ProductQuestionData();
        responseData.setQuestion("q?");

        when(service.createQuestion("p1", "q?")).thenReturn(model);
        when(converter.convert(model)).thenReturn(responseData);

        ProductQuestionData result = facade.createQuestion(requestData);

        assertSame(responseData, result);
        verify(service).createQuestion("p1", "q?");
        verify(converter).convert(model);
    }
}