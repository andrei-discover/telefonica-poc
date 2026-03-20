package br.com.telefonica.facades.productquestion.populator;

import br.com.telefonica.core.enums.QuestionStatus;
import br.com.telefonica.core.model.ProductQuestionModel;
import br.com.telefonica.facades.productquestion.data.ProductQuestionData;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.CustomerModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Date;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class TelefonicaProductQuestionPopulatorTest {

    private TelefonicaProductQuestionPopulator populator;

    @Before
    public void setUp() {
        populator = new TelefonicaProductQuestionPopulator();
    }

    @Test
    public void populate_ShouldMapAllFields_WhenSourceIsComplete() {
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
    public void populate_ShouldHandleNullFieldsGracefully() {
        ProductQuestionModel source = mock(ProductQuestionModel.class);
        ProductModel product = mock(ProductModel.class);
        CustomerModel customer = mock(CustomerModel.class);

        when(source.getProduct()).thenReturn(product);
        when(product.getCode()).thenReturn(null);
        when(source.getCustomer()).thenReturn(customer);
        when(customer.getUid()).thenReturn(null);
        when(source.getQuestion()).thenReturn(null);
        when(source.getStatus()).thenReturn(null);
        when(source.getCreationtime()).thenReturn(null);

        ProductQuestionData target = new ProductQuestionData();

        populator.populate(source, target);

        assertNull(target.getProductCode());
        assertNull(target.getAuthor());
        assertNull(target.getQuestion());
        assertNull(target.getStatus());
        assertNull(target.getCreatedDate());
    }

    @Test
    public void populate_ShouldConvertAllEnumValues() {
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

            assertEquals("Teste enum " + status, target.getQuestion());
            assertEquals(status.name(), target.getStatus());
            assertEquals("PROD_123_" + status, target.getProductCode());
            assertEquals("user_" + status, target.getAuthor());
        }
    }
}