package br.com.telefonica.controllers;

import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.product.data.ReviewData;
import de.hybris.platform.commerceservices.request.mapping.annotation.RequestMappingOverride;
import org.springframework.validation.Validator;
import de.hybris.platform.commercewebservicescommons.dto.product.ReviewWsDTO;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;



@RestController
@Tag(name = "Telefonica Products Controller")
@RequestMapping(value = "/{baseSiteId}/products")
public class TelefonicaProductsController extends BaseController {

	@Resource(name = "reviewDTOValidator")
	private Validator reviewDTOValidator;
	@Resource(name = "cwsProductFacade")
	private ProductFacade productFacade;

	@Secured({"ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT"})
	@RequestMappingOverride
	@RequestMapping(value = "/{productCode}/reviews", method = RequestMethod.POST, consumes = {MediaType.APPLICATION_JSON_VALUE,
		MediaType.APPLICATION_XML_VALUE})
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	@Operation(operationId = "createProductReview", summary = "Creates a customer review as an anonymous user.", description = "Creates a customer review for a product as an anonymous user.")
	@ApiBaseSiteIdParam
	public ReviewWsDTO createProductReview(
		@Parameter(description = "Product identifier.", required = true) @PathVariable final String productCode,
		@Parameter(description = "Object contains review details like : rating, alias, headline, comment.", required = true) @RequestBody final ReviewWsDTO review,
		@ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		validate(review, "review", reviewDTOValidator);
		final ReviewData reviewData = getDataMapper().map(review, ReviewData.class, "alias,rating,headline,comment");
		final ReviewData reviewDataRet = productFacade.postReview(productCode, reviewData);
		return getDataMapper().map(reviewDataRet, ReviewWsDTO.class, fields);
	}

}
