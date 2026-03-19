package br.com.telefonica.facades.productquestion.populator;

import br.com.telefonica.facades.productquestion.data.ProductQuestionData;
import br.com.telefonica.facades.productquestion.dto.ProductQuestionRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TelefonicaProductQuestionRequestPopulatorTest {

    private ProductQuestionRequestPopulator populator;

    @BeforeEach
    void setUp() {
        populator = new ProductQuestionRequestPopulator();
    }

    @Test
    void populate_shouldCopyAllFields() {
        ProductQuestionRequestDTO source = new ProductQuestionRequestDTO();
        source.setProductCode("PROD456");
        source.setQuestion("O produto é à prova d'água?");

        ProductQuestionData target = new ProductQuestionData();

        populator.populate(source, target);

        assertEquals("PROD456", target.getProductCode());
        assertEquals("O produto é à prova d'água?", target.getQuestion());
    }

    @Test
    void populate_shouldDoNothingIfSourceIsNull() {
        ProductQuestionData target = new ProductQuestionData();

        assertDoesNotThrow(() -> populator.populate(null, target));
        assertNull(target.getProductCode());
        assertNull(target.getQuestion());
    }

    @Test
    void populate_shouldDoNothingIfTargetIsNull() {
        ProductQuestionRequestDTO source = new ProductQuestionRequestDTO();
        source.setProductCode("PROD456");
        source.setQuestion("O produto é à prova d'água?");

        assertDoesNotThrow(() -> populator.populate(source, null));
    }
}