package br.com.telefonica.facades.productquestion.impl;

import br.com.telefonica.core.model.ProductQuestionModel;
import br.com.telefonica.core.service.ProductQuestionService;
import br.com.telefonica.facades.productquestion.ProductQuestionFacade;
import br.com.telefonica.facades.productquestion.dto.ProductQuestionRequestDTO;
import br.com.telefonica.facades.productquestion.dto.ProductQuestionResponseDTO;
import de.hybris.platform.servicelayer.dto.converter.Converter;

public class DefaultProductQuestionFacade implements ProductQuestionFacade {

    private ProductQuestionService productQuestionService;
    private Converter<ProductQuestionModel, ProductQuestionResponseDTO> productQuestionConverter;

    /**
     * Cria uma pergunta para um produto.
     * Recebe baseSiteId para conformidade com padrão OCC.
     */
    @Override
    public ProductQuestionResponseDTO createQuestion(String baseSiteId, ProductQuestionRequestDTO requestDTO) {

        final ProductQuestionModel model = productQuestionService.createQuestion(
                requestDTO.getProductCode(),
                requestDTO.getQuestion()
        );

        return productQuestionConverter.convert(model);
    }

    public void setProductQuestionService(ProductQuestionService productQuestionService) {
        this.productQuestionService = productQuestionService;
    }

    public void setProductQuestionConverter(
            Converter<ProductQuestionModel, ProductQuestionResponseDTO> productQuestionConverter) {
        this.productQuestionConverter = productQuestionConverter;
    }
}