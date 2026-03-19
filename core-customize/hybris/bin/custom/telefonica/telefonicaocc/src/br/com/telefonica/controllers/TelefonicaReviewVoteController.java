package br.com.telefonica.controllers;

import br.com.telefonica.facades.review.TelefonicaReviewVoteFacade;
import br.com.telefonica.occ.review.ReviewVoteWsDTO;
import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.cache.CacheControlDirective;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.validation.Validator;

import javax.annotation.Resource;

@Controller
@RequestMapping(value = "/{baseSiteId}/users/{userId}/reviews")
@CacheControl(directive = CacheControlDirective.PRIVATE)
@Tag(name = "Customer Review Vote")
public class TelefonicaReviewVoteController extends BaseController
{
	private static final Logger LOG = LoggerFactory.getLogger(TelefonicaReviewVoteController.class);

	@Resource(name = "telefonicaReviewVoteFacade")
	private TelefonicaReviewVoteFacade telefonicaReviewVoteFacade;

	@Resource(name = "telefonicaReviewVoteValidator")
	private Validator telefonicaReviewVoteValidator;

	@Secured({"ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT"})
	@PostMapping(value = "/{reviewId}/vote", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
	@ResponseBody
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(operationId = "voteHelpful", summary = "Vote on a review", description = "Authenticated user votes a review as helpful. Only one vote per review is allowed.")
	@ApiBaseSiteIdParam
	public void voteHelpful(@Parameter(description = "Review identifier.", required = true) @PathVariable final String reviewId) {
		validate(reviewId, "reviewId", telefonicaReviewVoteValidator);
		try	{
			telefonicaReviewVoteFacade.voteHelpful(reviewId);
		} catch (final IllegalStateException e)	{
			LOG.warn("Duplicate vote attempt — reviewId={} error={}", reviewId, e.getMessage());
			throw new IllegalStateException(e.getMessage());
		}
	}

	@Secured({"ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT"})
	@GetMapping(value = "/{reviewPk}/vote", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	@Operation(operationId = "hasVoted", summary = "Check if user voted", description = "Returns whether the authenticated user has already voted on this review.")
	@ApiBaseSiteIdParam
	public ReviewVoteWsDTO hasVoted(@Parameter(description = "Review PK.", required = true) @PathVariable final String reviewPk) {

		return getDataMapper().map(telefonicaReviewVoteFacade.getVoteData(reviewPk),ReviewVoteWsDTO.class);
	}
}
