package br.com.telefonica.core.dao.impl;

import br.com.telefonica.core.dao.TelefonicaProductQuestionDao;
import br.com.telefonica.core.model.ProductQuestionModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.List;

public class TelefonicaDefaultProductQuestionDao implements TelefonicaProductQuestionDao {

    private FlexibleSearchService flexibleSearchService;

    @Override
    public List<ProductQuestionModel> findQuestionsByProductCode(final String productCode) {
        final String query =
                "SELECT {pq.pk} " +
                        "FROM {ProductQuestion AS pq " +
                        "JOIN Product AS p ON {pq.product} = {p.pk}} " +
                        "WHERE {p.code} = ?productCode " +
                        "ORDER BY {pq.creationtime} DESC";

        final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(query);
        searchQuery.addQueryParameter("productCode", productCode);

        SearchResult<ProductQuestionModel> result = flexibleSearchService.search(searchQuery);
        return result.getResult();
    }

    /**
     * Setter para injeção do FlexibleSearchService.
     *
     * @param flexibleSearchService serviço de busca flexível do Hybris
     */
    public void setFlexibleSearchService(FlexibleSearchService flexibleSearchService) {
        this.flexibleSearchService = flexibleSearchService;
    }
}