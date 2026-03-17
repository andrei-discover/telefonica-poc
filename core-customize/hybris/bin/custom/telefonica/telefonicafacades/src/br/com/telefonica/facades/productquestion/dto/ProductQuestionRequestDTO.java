package br.com.telefonica.facades.productquestion.dto;

public class ProductQuestionRequestDTO {
    private String productCode;
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