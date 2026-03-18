package br.com.telefonica.controllers;

import br.com.telefonica.facades.productquestion.ProductQuestionFacade;
import br.com.telefonica.facades.productquestion.data.ProductQuestionData;
import br.com.telefonica.facades.productquestion.dto.ProductQuestionRequestDTO;
import br.com.telefonica.facades.productquestion.dto.ProductQuestionResponseDTO;
import br.com.telefonica.validators.ProductQuestionValidator;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/{baseSiteId}/productquestions")
@Tag(name = "Product Questions", description = "Gerencia perguntas relacionadas a produtos.")
public class ProductQuestionController {

    @Resource(name = "productQuestionFacade")
    private ProductQuestionFacade productQuestionFacade;

    @Resource(name = "productQuestionRequestConverter")
    private Converter<ProductQuestionRequestDTO, ProductQuestionData> requestConverter;

    @Resource(name = "productQuestionResponseConverter")
    private Converter<ProductQuestionData, ProductQuestionResponseDTO> responseConverter;

    @Resource(name = "productQuestionValidator")
    private ProductQuestionValidator productQuestionValidator;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            operationId = "createProductQuestion",
            summary = "Cria uma pergunta para um produto",
            description = "Permite que um usuário autenticado crie uma pergunta para um produto."
    )
    public ProductQuestionResponseDTO createQuestion(
            @PathVariable final String baseSiteId,
            @RequestBody @Valid final ProductQuestionRequestDTO requestDTO,
            final BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new WebserviceValidationException(bindingResult);
        }
        final ProductQuestionData requestData = requestConverter.convert(requestDTO);
        final ProductQuestionData responseData = productQuestionFacade.createQuestion(baseSiteId, requestData);

        return responseConverter.convert(responseData);
    }

    @GetMapping
    @Operation(
            operationId = "getProductQuestions",
            summary = "Lista perguntas de um produto",
            description = "Retorna todas as perguntas feitas para um produto específico."
    )
    public List<ProductQuestionResponseDTO> getQuestions(
            @PathVariable final String baseSiteId,
            @RequestParam("productCode") final String productCode) {

        final List<ProductQuestionData> questionsData = productQuestionFacade.getQuestionsForProduct(baseSiteId, productCode);

        return questionsData.stream()
                .map(responseConverter::convert)
                .toList();
    }

    @InitBinder
    protected void initBinder(final WebDataBinder binder) {
        binder.addValidators(productQuestionValidator);
    }
}