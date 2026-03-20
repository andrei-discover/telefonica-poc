package br.com.telefonica.controllers;

import br.com.telefonica.facades.productquestion.TelefonicaProductQuestionFacade;
import br.com.telefonica.facades.productquestion.data.ProductQuestionData;
import br.com.telefonica.facades.productquestion.dto.ProductQuestionRequestWsDTO;
import br.com.telefonica.facades.productquestion.dto.ProductQuestionResponseWsDTO;
import br.com.telefonica.validators.TelefonicaProductQuestionValidator;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class TelefonicaProductQuestionControllerTest {

    @Mock
    private TelefonicaProductQuestionFacade productQuestionFacade;

    @Mock
    private TelefonicaProductQuestionValidator productQuestionValidator;

    @Mock
    private DataMapper dataMapper;

    @InjectMocks
    private TelefonicaProductQuestionController controller;

    @Before
    public void setUp() {
        controller.setDataMapper(dataMapper);
    }

    @Test
    public void testCreateQuestion() {
        ProductQuestionRequestWsDTO requestDTO = new ProductQuestionRequestWsDTO();
        requestDTO.setProductCode("123");
        requestDTO.setQuestion("Qual a cor deste produto?");

        ProductQuestionData mappedRequest = new ProductQuestionData();
        mappedRequest.setProductCode("123");
        mappedRequest.setQuestion("Qual a cor deste produto?");

        ProductQuestionData responseData = new ProductQuestionData();
        responseData.setProductCode("123");
        responseData.setQuestion("Qual a cor deste produto?");
        responseData.setStatus("PENDING");

        ProductQuestionResponseWsDTO responseDTO = new ProductQuestionResponseWsDTO();
        responseDTO.setProductCode("123");
        responseDTO.setQuestion("Qual a cor deste produto?");
        responseDTO.setStatus("PENDING");

        when(dataMapper.map(requestDTO, ProductQuestionData.class)).thenReturn(mappedRequest);
        when(productQuestionFacade.createQuestion(mappedRequest)).thenReturn(responseData);
        when(dataMapper.map(responseData, ProductQuestionResponseWsDTO.class)).thenReturn(responseDTO);

        ProductQuestionResponseWsDTO result = controller.createQuestion(requestDTO);

        assertNotNull(result);
        assertEquals("123", result.getProductCode());
        assertEquals("Qual a cor deste produto?", result.getQuestion());
        assertEquals("PENDING", result.getStatus());

        verify(productQuestionValidator).validate(eq(requestDTO), any());
        verify(productQuestionFacade).createQuestion(mappedRequest);
        verify(dataMapper).map(requestDTO, ProductQuestionData.class);
        verify(dataMapper).map(responseData, ProductQuestionResponseWsDTO.class);
    }

    @Test
    public void testGetApprovedQuestions() {
        // apenas perguntas APPROVED devem ser retornadas
        ProductQuestionData q1 = new ProductQuestionData();
        q1.setProductCode("123");
        q1.setQuestion("É resistente à água?");
        q1.setStatus("APPROVED");

        ProductQuestionResponseWsDTO r1 = new ProductQuestionResponseWsDTO();
        r1.setProductCode("123");
        r1.setQuestion("É resistente à água?");
        r1.setStatus("APPROVED");

        // Mock do facade agora retorna apenas perguntas APPROVED
        when(productQuestionFacade.getApprovedQuestionsForProduct("123")).thenReturn(List.of(q1));
        when(dataMapper.map(q1, ProductQuestionResponseWsDTO.class)).thenReturn(r1);

        List<ProductQuestionResponseWsDTO> responses = controller.getQuestions("123");

        assertEquals(1, responses.size());
        assertEquals("É resistente à água?", responses.get(0).getQuestion());
        assertEquals("APPROVED", responses.get(0).getStatus());

        verify(productQuestionFacade).getApprovedQuestionsForProduct("123");
        verify(dataMapper).map(q1, ProductQuestionResponseWsDTO.class);
    }
}