package br.com.telefonica.core.dao;

import br.com.telefonica.core.dao.impl.DefaultProductQuestionDao;
import br.com.telefonica.core.model.ProductQuestionModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TelefonicaDefaultProductQuestionDaoTest {

    private DefaultProductQuestionDao dao;
    private FlexibleSearchService flexibleSearchService;

    @BeforeEach
    void setup() {
        flexibleSearchService = mock(FlexibleSearchService.class);
        dao = new DefaultProductQuestionDao();
        dao.setFlexibleSearchService(flexibleSearchService);
    }

    @Test
    @DisplayName("Deve construir query e retornar perguntas do produto")
    void findQuestionsByProductCode_buildsQueryAndReturnsResult() {
        ProductQuestionModel p1 = new ProductQuestionModel();
        List<ProductQuestionModel> expected = Arrays.asList(p1);

        @SuppressWarnings("unchecked")
        SearchResult<ProductQuestionModel> mockResult = mock(SearchResult.class);
        when(mockResult.getResult()).thenReturn(expected);

        when(flexibleSearchService.<ProductQuestionModel>search(any(FlexibleSearchQuery.class)))
                .thenReturn(mockResult);

        List<ProductQuestionModel> actual = dao.findQuestionsByProductCode("myCode");

        assertSame(expected, actual);

        ArgumentCaptor<FlexibleSearchQuery> captor = ArgumentCaptor.forClass(FlexibleSearchQuery.class);
        verify(flexibleSearchService).search(captor.capture());

        FlexibleSearchQuery used = captor.getValue();
        String q = used.getQuery();
        assertTrue(q.contains("WHERE {p.code} = ?productCode"));
        assertTrue(q.contains("ORDER BY {pq.creationtime} DESC"));
    }
}