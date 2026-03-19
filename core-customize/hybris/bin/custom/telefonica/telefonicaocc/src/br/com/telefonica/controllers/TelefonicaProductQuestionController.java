package br.com.telefonica.controllers;

import br.com.telefonica.facades.productquestion.TelefonicaProductQuestionFacade;
import br.com.telefonica.facades.productquestion.data.ProductQuestionData;
import br.com.telefonica.facades.productquestion.dto.ProductQuestionRequestWsDTO;
import br.com.telefonica.facades.productquestion.dto.ProductQuestionResponseWsDTO;
import br.com.telefonica.validators.TelefonicaProductQuestionValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping(value = "/{baseSiteId}/productquestions")
@Tag(name = "Product Questions", description = "Manages product-related questions.")
public class TelefonicaProductQuestionController extends BaseController {

    @Resource(name = "productQuestionFacade")
    private TelefonicaProductQuestionFacade productQuestionFacade;

    @Resource(name = "productQuestionValidator")
    private TelefonicaProductQuestionValidator productQuestionValidator;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            operationId = "createProductQuestion",
            summary = "Create a question for a product",
            description = "Allows an authenticated user to create a question for a product."
    )
    public ProductQuestionResponseWsDTO createQuestion(@RequestBody final ProductQuestionRequestWsDTO requestDTO) {

        validate(requestDTO, "ProductQuestionRequestWsDTO", productQuestionValidator);

        ProductQuestionData requestData = getDataMapper().map(requestDTO, ProductQuestionData.class);
        ProductQuestionData responseData = productQuestionFacade.createQuestion(requestData);

        return getDataMapper().map(responseData, ProductQuestionResponseWsDTO.class);
    }


    @GetMapping
    @Operation(
            operationId = "getProductQuestions",
            summary = "List product questions",
            description = "Returns all questions submitted for a specific product."
    )
    public List<ProductQuestionResponseWsDTO> getQuestions(@RequestParam("productCode") final String productCode) {

        final List<ProductQuestionData> questionsData = productQuestionFacade.getQuestionsForProduct(productCode);

        return questionsData.stream()
                .map(q -> getDataMapper().map(q, ProductQuestionResponseWsDTO.class))
                .toList();
    }

}