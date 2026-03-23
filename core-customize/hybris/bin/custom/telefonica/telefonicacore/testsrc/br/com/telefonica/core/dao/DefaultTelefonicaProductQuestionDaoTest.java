package br.com.telefonica.core.dao;

import br.com.telefonica.core.dao.impl.DefaultTelefonicaProductQuestionDao;
import br.com.telefonica.core.enums.QuestionStatus;
import br.com.telefonica.core.model.ProductQuestionModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultTelefonicaProductQuestionDaoTest
{

    @Mock
    private FlexibleSearchService flexibleSearchService;

    @InjectMocks
    private DefaultTelefonicaProductQuestionDao dao;

    @Before
    public void setUp() {
    }


    @Test
    public void findQuestionsByProductAndStatus_buildsQueryAndReturnsResult() {

        ProductModel product = new ProductModel();

        ProductQuestionModel p1 = new ProductQuestionModel();
        List<ProductQuestionModel> expected = Arrays.asList(p1);

        @SuppressWarnings("unchecked")
        SearchResult<ProductQuestionModel> mockResult = mock(SearchResult.class);

        when(mockResult.getResult()).thenReturn(expected);
        when(flexibleSearchService.<ProductQuestionModel>search(any(FlexibleSearchQuery.class)))
                .thenReturn(mockResult);

        List<ProductQuestionModel> actual =
                dao.findQuestionsByProductCodeAndStatus(product, QuestionStatus.APPROVED);

        assertSame(expected, actual);

        ArgumentCaptor<FlexibleSearchQuery> captor =
                ArgumentCaptor.forClass(FlexibleSearchQuery.class);

        verify(flexibleSearchService).search(captor.capture());

        FlexibleSearchQuery used = captor.getValue();
        String q = used.getQuery();

        assertTrue(q.contains("FROM {ProductQuestion AS pq}"));
        assertTrue(q.contains("WHERE {pq.product} = ?product AND {pq.status} = ?status"));
        assertTrue(q.contains("ORDER BY {pq.creationtime} DESC"));

        assertSame(product, used.getQueryParameters().get("product"));
        assertSame(QuestionStatus.APPROVED, used.getQueryParameters().get("status"));
    }
}
