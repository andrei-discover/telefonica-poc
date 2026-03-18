package br.com.telefonica.facades.productquestion.dto;

import de.hybris.platform.validation.annotations.NotBlank;

public class ProductQuestionRequestDTO {
    @NotBlank(message = "O código do produto é obrigatório")
    private String productCode;

    @NotBlank(message = "A pergunta não pode estar vazia")
    private String question;

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
}