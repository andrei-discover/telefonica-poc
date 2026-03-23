package br.com.telefonica.validators;

import br.com.telefonica.facades.productquestion.dto.ProductQuestionRequestWsDTO;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.Errors;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class TelefonicaProductQuestionValidatorTest
{
	@Mock
	private Errors errors;

	@InjectMocks
	private TelefonicaProductQuestionValidator validator;

	private ProductQuestionRequestWsDTO dto;

	@Before
	public void setUp()
	{
		dto = new ProductQuestionRequestWsDTO();
		dto.setProductCode("iPhone_13_blue_256g");
		dto.setQuestion("O produto tem garantia?");
	}

	@Test
	public void testSupportsProductQuestionRequestWsDTO()
	{
		// When
		final boolean result = validator.supports(ProductQuestionRequestWsDTO.class);

		// Then
		assertTrue(result);
	}

	@Test
	public void testSupportsReturnsFalseForOtherClass()
	{
		// When
		final boolean result = validator.supports(Object.class);

		// Then
		assertFalse(result);
	}

	@Test
	public void testValidateWithValidDTO()
	{
		// When
		validator.validate(dto, errors);

		// Then
		verify(errors, never()).rejectValue(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyString(),
			org.mockito.ArgumentMatchers.anyString());
	}

	@Test
	public void testValidateWithNullProductCode()
	{
		// Given
		dto.setProductCode(null);

		// When
		validator.validate(dto, errors);

		// Then
		verify(errors).rejectValue("productCode", "field.required", "O código do produto é obrigatório");
	}

	@Test
	public void testValidateWithEmptyProductCode()
	{
		// Given
		dto.setProductCode("");

		// When
		validator.validate(dto, errors);

		// Then
		verify(errors).rejectValue("productCode", "field.required", "O código do produto é obrigatório");
	}

	@Test
	public void testValidateWithBlankProductCode()
	{
		// Given
		dto.setProductCode("   ");

		// When
		validator.validate(dto, errors);

		// Then
		verify(errors).rejectValue("productCode", "field.required", "O código do produto é obrigatório");
	}

	@Test
	public void testValidateWithNullQuestion()
	{
		// Given
		dto.setQuestion(null);

		// When
		validator.validate(dto, errors);

		// Then
		verify(errors).rejectValue("question", "field.required", "A pergunta não pode estar vazia");
	}

	@Test
	public void testValidateWithEmptyQuestion()
	{
		// Given
		dto.setQuestion("");

		// When
		validator.validate(dto, errors);

		// Then
		verify(errors).rejectValue("question", "field.required", "A pergunta não pode estar vazia");
	}

	@Test
	public void testValidateWithBlankQuestion()
	{
		// Given
		dto.setQuestion("   ");

		// When
		validator.validate(dto, errors);

		// Then
		verify(errors).rejectValue("question", "field.required", "A pergunta não pode estar vazia");
	}

	@Test
	public void testValidateWithNullProductCodeAndNullQuestion()
	{
		// Given
		dto.setProductCode(null);
		dto.setQuestion(null);

		// When
		validator.validate(dto, errors);

		// Then
		verify(errors).rejectValue("productCode", "field.required", "O código do produto é obrigatório");

		verify(errors).rejectValue("question", "field.required", "A pergunta não pode estar vazia");
	}
}
