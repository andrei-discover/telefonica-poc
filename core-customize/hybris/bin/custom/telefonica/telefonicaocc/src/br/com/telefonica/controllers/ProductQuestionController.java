package br.com.telefonica.controllers;

import br.com.telefonica.facades.productquestion.ProductQuestionFacade;
import br.com.telefonica.facades.productquestion.dto.ProductQuestionRequestDTO;
import br.com.telefonica.facades.productquestion.dto.ProductQuestionResponseDTO;

//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
//import io.swagger.annotations.ApiParam;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping(value = "/{baseSiteId}/productquestions")
//@Api(tags = "Product Questions")
public class ProductQuestionController {

    @Resource(name = "productQuestionFacade")
    private ProductQuestionFacade productQuestionFacade;

    /**
     * Cria uma pergunta para um produto.
     * Apenas usuários autenticados com ROLE_CUSTOMER podem acessar este endpoint.
     *
     * @param baseSiteId código do site atual (padrão OCC, usado no contexto do site)
     * @param requestDTO objeto com dados da pergunta
     * @return ProductQuestionResponseDTO criado
     */
    @PostMapping
    //@ApiOperation(value = "Create a question for a product")
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    public ProductQuestionResponseDTO createQuestion(
            @PathVariable final String baseSiteId,
            //@ApiParam(value = "Question request object", required = true)
            @RequestBody @Valid final ProductQuestionRequestDTO requestDTO) {

        return productQuestionFacade.createQuestion(baseSiteId, requestDTO);
    }
}