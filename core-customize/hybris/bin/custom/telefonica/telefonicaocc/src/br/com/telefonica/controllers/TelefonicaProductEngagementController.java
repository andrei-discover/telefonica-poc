package br.com.telefonica.controllers;

import br.com.telefonica.facades.engagement.TelefonicaProductEngagementFacade;
import br.com.telefonica.facades.product.engagement.ProductEngagementSummaryData;
import br.com.telefonica.occ.dto.product.engagement.ProductEngagementSummaryWsDTO;
import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.cache.CacheControlDirective;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;

import javax.annotation.Resource;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;


@Controller
@RequestMapping(value = "/{baseSiteId}/products")
@CacheControl(directive = CacheControlDirective.PRIVATE)
@Tag(name = "Product Engagement Summary")
public class TelefonicaProductEngagementController extends BaseController
{
	private static final int DEFAULT_TOP_N = 5;

	@Resource(name = "telefonicaProductEngagementFacade")
	private TelefonicaProductEngagementFacade telefonicaProductEngagementFacade;

	@Secured({"ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT"})
	@GetMapping(value = "/{productCode}/engagement-summary", produces = {MediaType.APPLICATION_JSON_VALUE,
		MediaType.APPLICATION_XML_VALUE})
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	@Operation(operationId = "getEngagementSummary", summary = "Get product engagement summary", description = "Returns engagement summary for a product including average rating, rating distribution, verified purchase percentage, top helpful reviews, total questions, question publication rate and average answer time.")
	@ApiBaseSiteIdParam
	public ProductEngagementSummaryWsDTO getEngagementSummary(
		@Parameter(description = "Product identifier.", required = true) @PathVariable final String productCode,
		@Parameter(description = "Number of top helpful reviews to return.") @RequestParam(defaultValue = "5") final int topN,
		@ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		final ProductEngagementSummaryData summaryData =
			telefonicaProductEngagementFacade.getEngagementSummary(productCode, topN);

		return getDataMapper().map(summaryData, ProductEngagementSummaryWsDTO.class, fields);
	}
}
