package br.com.telefonica.facades.productquestion;

import br.com.telefonica.facades.productquestion.dto.ProductQuestionRequestDTO;
import br.com.telefonica.facades.productquestion.dto.ProductQuestionResponseDTO;

public interface ProductQuestionFacade {

    /**
     * Cria uma pergunta para um produto.
     * A pergunta nasce com status PENDING e deve ser moderada antes da publicação.
     *
     * @param requestDTO DTO contendo o código do produto e o texto da pergunta
     * @return DTO com os dados da pergunta criada, incluindo status e autor
     */
    ProductQuestionResponseDTO createQuestion(String baseSiteId, ProductQuestionRequestDTO requestDTO);
}
