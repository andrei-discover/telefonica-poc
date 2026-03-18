package br.com.telefonica.facades.productquestion.impl;

import br.com.telefonica.core.model.ProductQuestionModel;
import br.com.telefonica.core.service.ProductQuestionService;
import br.com.telefonica.facades.productquestion.ProductQuestionFacade;
import br.com.telefonica.facades.productquestion.data.ProductQuestionData;
import br.com.telefonica.facades.productquestion.dto.ProductQuestionResponseDTO;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.List;
import java.util.stream.Collectors;

public class DefaultProductQuestionFacade implements ProductQuestionFacade {

    private ProductQuestionService productQuestionService;
    private Converter<ProductQuestionModel, ProductQuestionData> productQuestionConverter;

    @Override
    public ProductQuestionData createQuestion(String baseSiteId, ProductQuestionData requestDTO) {

        final ProductQuestionModel model = productQuestionService.createQuestion(
                requestDTO.getProductCode(),
                requestDTO.getQuestion()
        );

        return productQuestionConverter.convert(model);
    }

    @Override
    public List<ProductQuestionData> getQuestionsForProduct(String baseSiteId, String productCode) {
        final List<ProductQuestionModel> questions = productQuestionService.getQuestionsForProduct(productCode);

        return questions.stream()
                .map(productQuestionConverter::convert)
                .collect(Collectors.toList());
    }

    public void setProductQuestionService(ProductQuestionService productQuestionService) {
        this.productQuestionService = productQuestionService;
    }

    public void setProductQuestionConverter(Converter<ProductQuestionModel, ProductQuestionData> productQuestionConverter) {
        this.productQuestionConverter = productQuestionConverter;
    }
}