package br.com.telefonica.controllers;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.webservicescommons.dto.error.ErrorListWsDTO;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import static org.junit.Assert.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class BaseControllerTest extends BaseController {

    @Test
    public void testSanitize() {
        String input = "<script>alert('x')</script>";

        String result = sanitize(input);

        assertNotNull(result);
        assertFalse(result.contains("<script>"));
    }

    @Test
    public void testHandleModelNotFoundException() {
        Exception ex = new ModelNotFoundException("Product not found");

        ErrorListWsDTO result = handleModelNotFoundException(ex);

        assertNotNull(result);
        assertEquals(1, result.getErrors().size());
        assertEquals("UnknownIdentifierError", result.getErrors().get(0).getType());
        assertTrue(result.getErrors().get(0).getMessage().contains("Product not found"));
    }

    @Test
    public void testHandleErrorInternal() {
        ErrorListWsDTO result = handleErrorInternal("SomeException", "Erro ocorreu");

        assertNotNull(result);
        assertEquals(1, result.getErrors().size());
        assertEquals("SomeError", result.getErrors().get(0).getType());
        assertEquals("Erro ocorreu", result.getErrors().get(0).getMessage());
    }

    @Test
    public void testValidateSuccess() {
        Object obj = new Object();

        Validator validator = new Validator() {
            @Override
            public boolean supports(Class<?> clazz) {
                return true;
            }

            @Override
            public void validate(Object target, Errors errors) {
            }
        };

        validate(obj, "obj", validator);
    }

    @Test(expected = WebserviceValidationException.class)
    public void testValidateWithError() {
        Object obj = new Object();

        Validator validator = new Validator() {
            @Override
            public boolean supports(Class<?> clazz) {
                return true;
            }

            @Override
            public void validate(Object target, Errors errors) {
                errors.reject("error.code");
            }
        };

        validate(obj, "obj", validator);
    }
}