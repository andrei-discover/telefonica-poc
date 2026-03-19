package br.com.telefonica.controllers;

import br.com.telefonica.facades.productquestion.TelefonicaProductQuestionFacade;
import br.com.telefonica.facades.productquestion.data.ProductQuestionData;
import br.com.telefonica.facades.productquestion.dto.ProductQuestionRequestDTO;
import br.com.telefonica.facades.productquestion.dto.ProductQuestionResponseDTO;
import br.com.telefonica.validators.TelefonicaProductQuestionValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping(value = "/{baseSiteId}/productquestions")
@Tag(name = "Product Questions", description = "Gerencia perguntas relacionadas a produtos.")
public class TelefonicaProductQuestionController extends BaseController {

    @Resource(name = "productQuestionFacade")
    private TelefonicaProductQuestionFacade productQuestionFacade;

    @Resource(name = "productQuestionValidator")
    private TelefonicaProductQuestionValidator productQuestionValidator;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            operationId = "createProductQuestion",
            summary = "Cria uma pergunta para um produto",
            description = "Permite que um usuário autenticado crie uma pergunta para um produto."
    )
    public ProductQuestionResponseDTO createQuestion(@RequestBody final ProductQuestionRequestDTO requestDTO) {

        validate(requestDTO, "ProductQuestionRequestDTO", productQuestionValidator);

        ProductQuestionData requestData = getDataMapper().map(requestDTO, ProductQuestionData.class);
        ProductQuestionData responseData = productQuestionFacade.createQuestion(requestData);

        return getDataMapper().map(responseData, ProductQuestionResponseDTO.class);
    }


    @GetMapping
    @Operation(
            operationId = "getProductQuestions",
            summary = "Lista perguntas de um produto",
            description = "Retorna todas as perguntas feitas para um produto específico."
    )
    public List<ProductQuestionResponseDTO> getQuestions(@RequestParam("productCode") final String productCode) {

        final List<ProductQuestionData> questionsData = productQuestionFacade.getQuestionsForProduct(productCode);

        return questionsData.stream()
                .map(q -> getDataMapper().map(q, ProductQuestionResponseDTO.class))
                .toList();
    }

}