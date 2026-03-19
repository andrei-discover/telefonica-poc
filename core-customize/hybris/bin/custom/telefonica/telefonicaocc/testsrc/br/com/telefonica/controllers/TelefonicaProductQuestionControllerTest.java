package br.com.telefonica.controllers;

import br.com.telefonica.facades.productquestion.ProductQuestionFacade;
import br.com.telefonica.facades.productquestion.data.ProductQuestionData;
import br.com.telefonica.facades.productquestion.dto.ProductQuestionRequestDTO;
import br.com.telefonica.facades.productquestion.dto.ProductQuestionResponseDTO;
import br.com.telefonica.validators.ProductQuestionValidator;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TelefonicaProductQuestionControllerTest {

    @InjectMocks
    private ProductQuestionController controller;

    @Mock
    private ProductQuestionFacade productQuestionFacade;

    @Mock
    private Converter<ProductQuestionRequestDTO, ProductQuestionData> requestConverter;

    @Mock
    private Converter<ProductQuestionData, ProductQuestionResponseDTO> responseConverter;

    @Mock
    private ProductQuestionValidator productQuestionValidator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createQuestion_shouldReturnResponse_whenValidRequest() {
        // Arrange
        String baseSiteId = "testSite";
        ProductQuestionRequestDTO requestDTO = new ProductQuestionRequestDTO();
        requestDTO.setProductCode("PROD123");
        requestDTO.setQuestion("Qual é a garantia?");

        ProductQuestionData requestData = new ProductQuestionData();
        ProductQuestionData responseData = new ProductQuestionData();
        ProductQuestionResponseDTO responseDTO = new ProductQuestionResponseDTO();

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(requestConverter.convert(requestDTO)).thenReturn(requestData);
        when(productQuestionFacade.createQuestion(baseSiteId, requestData)).thenReturn(responseData);
        when(responseConverter.convert(responseData)).thenReturn(responseDTO);

        ProductQuestionResponseDTO result = controller.createQuestion(baseSiteId, requestDTO, bindingResult);

        assertNotNull(result);
        assertEquals(responseDTO, result);

        verify(requestConverter).convert(requestDTO);
        verify(productQuestionFacade).createQuestion(baseSiteId, requestData);
        verify(responseConverter).convert(responseData);
    }

    @Test
    void createQuestion_shouldThrowValidationException_whenBindingResultHasErrors() {
        ProductQuestionRequestDTO requestDTO = new ProductQuestionRequestDTO();
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);

        assertThrows(WebserviceValidationException.class,
                () -> controller.createQuestion("testSite", requestDTO, bindingResult));

        verifyNoInteractions(requestConverter, productQuestionFacade, responseConverter);
    }

    @Test
    void getQuestions_shouldReturnConvertedList() {
        String baseSiteId = "testSite";
        String productCode = "PROD123";

        ProductQuestionData data1 = new ProductQuestionData();
        ProductQuestionData data2 = new ProductQuestionData();

        ProductQuestionResponseDTO dto1 = new ProductQuestionResponseDTO();
        ProductQuestionResponseDTO dto2 = new ProductQuestionResponseDTO();

        when(productQuestionFacade.getQuestionsForProduct(baseSiteId, productCode))
                .thenReturn(List.of(data1, data2));
        when(responseConverter.convert(data1)).thenReturn(dto1);
        when(responseConverter.convert(data2)).thenReturn(dto2);

        List<ProductQuestionResponseDTO> result = controller.getQuestions(baseSiteId, productCode);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(dto1, result.get(0));
        assertEquals(dto2, result.get(1));
    }

    @Test
    void initBinder_shouldAddValidator() {
        WebDataBinder binder = mock(WebDataBinder.class);
        controller.initBinder(binder);
        verify(binder).addValidators(productQuestionValidator);
    }
}