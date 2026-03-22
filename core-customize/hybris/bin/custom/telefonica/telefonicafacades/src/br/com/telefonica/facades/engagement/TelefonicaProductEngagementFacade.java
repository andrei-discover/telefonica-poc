package br.com.telefonica.facades.engagement;

import br.com.telefonica.facades.product.engagement.ProductEngagementSummaryData;


public interface TelefonicaProductEngagementFacade
{
	ProductEngagementSummaryData getEngagementSummary(String productCode, int topN);
}
