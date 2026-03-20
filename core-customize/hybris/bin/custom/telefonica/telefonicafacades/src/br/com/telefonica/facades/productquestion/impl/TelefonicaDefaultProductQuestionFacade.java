package br.com.telefonica.facades.productquestion.impl;

import br.com.telefonica.core.model.ProductQuestionModel;
import br.com.telefonica.core.service.TelefonicaProductQuestionService;
import br.com.telefonica.facades.productquestion.TelefonicaProductQuestionFacade;
import br.com.telefonica.facades.productquestion.data.ProductQuestionData;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.List;
import java.util.stream.Collectors;

public class TelefonicaDefaultProductQuestionFacade implements TelefonicaProductQuestionFacade {

    private TelefonicaProductQuestionService productQuestionService;
    private Converter<ProductQuestionModel, ProductQuestionData> productQuestionConverter;

    @Override
    public ProductQuestionData createQuestion(ProductQuestionData requestDTO) {

        final ProductQuestionModel model = productQuestionService.createQuestion(
                requestDTO.getProductCode(),
                requestDTO.getQuestion()
        );

        return productQuestionConverter.convert(model);
    }

    @Override
    public List<ProductQuestionData> getApprovedQuestionsForProduct(String productCode) {
        final List<ProductQuestionModel> questions = productQuestionService.getApprovedQuestionsForProduct(productCode);

        return questions.stream()
                .map(productQuestionConverter::convert)
                .collect(Collectors.toList());
    }

    public void setProductQuestionService(TelefonicaProductQuestionService productQuestionService) {
        this.productQuestionService = productQuestionService;
    }

    public void setProductQuestionConverter(Converter<ProductQuestionModel, ProductQuestionData> productQuestionConverter) {
        this.productQuestionConverter = productQuestionConverter;
    }
}