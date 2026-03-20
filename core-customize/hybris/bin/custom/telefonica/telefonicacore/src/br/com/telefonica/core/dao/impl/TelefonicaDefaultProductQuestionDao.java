package br.com.telefonica.core.dao.impl;

import br.com.telefonica.core.dao.TelefonicaProductQuestionDao;
import br.com.telefonica.core.enums.QuestionStatus;
import br.com.telefonica.core.model.ProductQuestionModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.List;

public class TelefonicaDefaultProductQuestionDao implements TelefonicaProductQuestionDao {

    private FlexibleSearchService flexibleSearchService;

    @Override
    public List<ProductQuestionModel> findQuestionsByProductCodeAndStatus(final ProductModel product, final QuestionStatus status) {

        final String query =
                "SELECT {pq.pk} " +
                        "FROM {ProductQuestion AS pq} " +
                        "WHERE {pq.product} = ?product " +
                        "AND {pq.status} = ?status " +
                        "ORDER BY {pq.creationtime} DESC";

        final FlexibleSearchQuery flexiQuery = new FlexibleSearchQuery(query);
        flexiQuery.addQueryParameter("product", product);
        flexiQuery.addQueryParameter("status", status);

        SearchResult<ProductQuestionModel> result = flexibleSearchService.search(flexiQuery);
        return result.getResult();
    }

    public void setFlexibleSearchService(FlexibleSearchService flexibleSearchService) {
        this.flexibleSearchService = flexibleSearchService;
    }
}