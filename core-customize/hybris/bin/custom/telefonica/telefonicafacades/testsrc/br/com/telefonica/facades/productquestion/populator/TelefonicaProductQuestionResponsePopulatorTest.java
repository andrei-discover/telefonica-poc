package br.com.telefonica.facades.productquestion.populator;

import br.com.telefonica.facades.productquestion.data.ProductQuestionData;
import br.com.telefonica.facades.productquestion.dto.ProductQuestionResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.junit.jupiter.api.Assertions.*;

public class TelefonicaProductQuestionResponsePopulatorTest {

    private ProductQuestionResponsePopulator populator;

    @BeforeEach
    void setUp() {
        populator = new ProductQuestionResponsePopulator();
    }

    @Test
    void populate_shouldCopyAllFields() {
        ProductQuestionData source = new ProductQuestionData();
        source.setProductCode("PROD123");
        source.setQuestion("Qual é a garantia?");
        source.setStatus("PENDING");
        source.setAuthor("João");

        Date createdDate = new GregorianCalendar(2026, Calendar.MARCH, 18, 10, 0).getTime();
        source.setCreatedDate(createdDate);

        ProductQuestionResponseDTO target = new ProductQuestionResponseDTO();

        populator.populate(source, target);

        assertEquals("PROD123", target.getProductCode());
        assertEquals("Qual é a garantia?", target.getQuestion());
        assertEquals("PENDING", target.getStatus());
        assertEquals("João", target.getAuthor());
        assertEquals(createdDate, target.getCreatedDate());
    }

    @Test
    void populate_shouldDoNothingIfSourceIsNull() {
        ProductQuestionResponseDTO target = new ProductQuestionResponseDTO();

        assertDoesNotThrow(() -> populator.populate(null, target));
        assertNull(target.getProductCode());
        assertNull(target.getQuestion());
        assertNull(target.getStatus());
        assertNull(target.getAuthor());
        assertNull(target.getCreatedDate());
    }

    @Test
    void populate_shouldDoNothingIfTargetIsNull() {
        ProductQuestionData source = new ProductQuestionData();
        source.setProductCode("PROD123");

        assertDoesNotThrow(() -> populator.populate(source, null));
    }
}