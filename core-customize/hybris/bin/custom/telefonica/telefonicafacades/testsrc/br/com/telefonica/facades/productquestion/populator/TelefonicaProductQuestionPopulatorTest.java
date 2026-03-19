package br.com.telefonica.facades.productquestion.populator;

import br.com.telefonica.core.enums.QuestionStatus;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.product.ProductModel;
import br.com.telefonica.core.model.ProductQuestionModel;
import br.com.telefonica.facades.productquestion.data.ProductQuestionData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TelefonicaProductQuestionPopulatorTest {

    private ProductQuestionPopulator populator;

    @BeforeEach
    void setUp() {
        populator = new ProductQuestionPopulator();
    }

    @Test
    @DisplayName("Deve mapear todos os campos corretamente quando o source estiver completo")
    void populate_ShouldMapAllFields_WhenSourceIsComplete() {
        ProductQuestionModel source = mock(ProductQuestionModel.class);
        ProductModel product = mock(ProductModel.class);
        CustomerModel customer = mock(CustomerModel.class);
        Date creationTime = new Date();

        when(source.getProduct()).thenReturn(product);
        when(product.getCode()).thenReturn("PROD_123");
        when(source.getCustomer()).thenReturn(customer);
        when(customer.getUid()).thenReturn("user123");
        when(source.getQuestion()).thenReturn("Esta é uma pergunta de teste");
        when(source.getStatus()).thenReturn(QuestionStatus.PENDING);
        when(source.getCreationtime()).thenReturn(creationTime);

        ProductQuestionData target = new ProductQuestionData();

        populator.populate(source, target);

        assertNotNull(target);
        assertEquals("PROD_123", target.getProductCode());
        assertEquals("user123", target.getAuthor());
        assertEquals("Esta é uma pergunta de teste", target.getQuestion());
        assertEquals("PENDING", target.getStatus());
        assertEquals(creationTime, target.getCreatedDate());
    }

    @Test
    @DisplayName("Deve lidar com campos internos nulos sem lançar exceção")
    void populate_ShouldHandleNullFieldsGracefully() {
        ProductQuestionModel source = mock(ProductQuestionModel.class);
        ProductModel product = mock(ProductModel.class);
        CustomerModel customer = mock(CustomerModel.class);

        // Os mocks existem, mas os métodos internos retornam null
        when(source.getProduct()).thenReturn(product);
        when(product.getCode()).thenReturn(null);
        when(source.getCustomer()).thenReturn(customer);
        when(customer.getUid()).thenReturn(null);
        when(source.getQuestion()).thenReturn(null);
        when(source.getStatus()).thenReturn(null);
        when(source.getCreationtime()).thenReturn(null);

        ProductQuestionData target = new ProductQuestionData();

        assertDoesNotThrow(() -> populator.populate(source, target));

        assertNull(target.getProductCode());
        assertNull(target.getAuthor());
        assertNull(target.getQuestion());
        assertNull(target.getStatus());
        assertNull(target.getCreatedDate());
    }

    @Test
    @DisplayName("Deve mapear todos os valores de QuestionStatus corretamente")
    void populate_ShouldConvertAllEnumValues() {
        for (QuestionStatus status : QuestionStatus.values()) {
            ProductQuestionModel source = mock(ProductQuestionModel.class);
            ProductModel product = mock(ProductModel.class);
            CustomerModel customer = mock(CustomerModel.class);

            when(source.getProduct()).thenReturn(product);
            when(product.getCode()).thenReturn("PROD_123_" + status);
            when(source.getCustomer()).thenReturn(customer);
            when(customer.getUid()).thenReturn("user_" + status);
            when(source.getQuestion()).thenReturn("Teste enum " + status);
            when(source.getStatus()).thenReturn(status);
            when(source.getCreationtime()).thenReturn(null);

            ProductQuestionData target = new ProductQuestionData();

            populator.populate(source, target);

            assertEquals("Teste enum " + status, target.getQuestion(), "O campo 'question' deve ser populado corretamente");
            assertEquals(status.name(), target.getStatus(), "O status deve ser convertido para string corretamente");
            assertEquals("PROD_123_" + status, target.getProductCode(), "O código do produto deve ser populado corretamente");
            assertEquals("user_" + status, target.getAuthor(), "O autor deve ser populado corretamente");
        }
    }
}