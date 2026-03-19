package br.com.telefonica.facades.productquestion;

import br.com.telefonica.core.model.ProductQuestionModel;
import br.com.telefonica.core.service.ProductQuestionService;
import br.com.telefonica.facades.productquestion.data.ProductQuestionData;
import br.com.telefonica.facades.productquestion.impl.DefaultProductQuestionFacade;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

public class TelefonicaDefaultProductQuestionFacadeTest {

    private DefaultProductQuestionFacade facade;
    private ProductQuestionService service;
    private Converter<ProductQuestionModel, ProductQuestionData> converter;

    @BeforeEach
    void setup() {
        service = mock(ProductQuestionService.class);
        converter = mock(Converter.class);

        facade = new DefaultProductQuestionFacade();
        facade.setProductQuestionService(service);
        facade.setProductQuestionConverter(converter);
    }

    @Test
    @DisplayName("Deve criar pergunta via Facade e retornar ProductQuestionData")
    void createQuestion_delegatesToServiceAndConverter() {
        ProductQuestionData requestData = new ProductQuestionData();
        requestData.setProductCode("p1");
        requestData.setQuestion("q?");

        ProductQuestionModel model = new ProductQuestionModel();

        ProductQuestionData responseData = new ProductQuestionData();
        responseData.setQuestion("q?");

        when(service.createQuestion("p1", "q?")).thenReturn(model);
        when(converter.convert(model)).thenReturn(responseData);

        ProductQuestionData result = facade.createQuestion("site", requestData);

        assertSame(responseData, result);
        verify(service).createQuestion("p1", "q?");
        verify(converter).convert(model);
    }
}