package br.com.telefonica.controllers;

import br.com.telefonica.facades.productquestion.ProductQuestionFacade;
import br.com.telefonica.facades.productquestion.dto.ProductQuestionRequestDTO;
import br.com.telefonica.facades.productquestion.dto.ProductQuestionResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * OCC Web Services Controller para Perguntas de Produtos (Product Questions)
 */
@RestController
@RequestMapping(value = "/{baseSiteId}/productquestions")
@Tag(name = "Product Questions", description = "Gerencia perguntas relacionadas a produtos.")
public class ProductQuestionController {

    @Resource(name = "productQuestionFacade")
    private ProductQuestionFacade productQuestionFacade;

    /**
     * Cria uma pergunta para um produto.
     * Apenas usuários autenticados com ROLE_CUSTOMER podem acessar este endpoint.
     *
     * @param baseSiteId código do site atual (padrão OCC)
     * @param requestDTO objeto com dados da pergunta
     * @return ProductQuestionResponseDTO criado
     */
    @PostMapping
    @Operation(
            operationId = "createProductQuestion",
            summary = "Cria uma pergunta para um produto",
            description = "Permite que um usuário autenticado crie uma pergunta para um produto."
    )
    public ProductQuestionResponseDTO createQuestion(
            @PathVariable final String baseSiteId,
            @RequestBody @Valid final ProductQuestionRequestDTO requestDTO) {

        return productQuestionFacade.createQuestion(baseSiteId, requestDTO);
    }
}