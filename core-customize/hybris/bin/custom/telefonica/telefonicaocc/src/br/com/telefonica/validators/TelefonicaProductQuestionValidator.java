package br.com.telefonica.validators;

import br.com.telefonica.facades.productquestion.dto.ProductQuestionRequestDTO;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class TelefonicaProductQuestionValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return ProductQuestionRequestDTO.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {

        ProductQuestionRequestDTO dto = (ProductQuestionRequestDTO) target;

        if (dto.getProductCode() == null || dto.getProductCode().trim().isEmpty()) {
            errors.rejectValue("productCode", "field.required", "O código do produto é obrigatório");
        }

        if (dto.getQuestion() == null || dto.getQuestion().trim().isEmpty()) {
            errors.rejectValue("question", "field.required", "A pergunta não pode estar vazia");
        }
    }
}