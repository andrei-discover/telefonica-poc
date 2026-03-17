package br.com.telefonica.core.dao.impl;

import br.com.telefonica.core.dao.ProductQuestionDao;
import br.com.telefonica.core.model.ProductQuestionModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import javax.annotation.Resource;
import java.util.List;

public class DefaultProductQuestionDao implements ProductQuestionDao {

    private FlexibleSearchService flexibleSearchService;

    /**
     * Retorna todas as perguntas de um produto identificado pelo seu código.
     * Ordena por data de criação, da mais recente para a mais antiga.
     *
     * @param productCode código do produto
     * @return lista de ProductQuestionModel associadas ao produto
     */
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