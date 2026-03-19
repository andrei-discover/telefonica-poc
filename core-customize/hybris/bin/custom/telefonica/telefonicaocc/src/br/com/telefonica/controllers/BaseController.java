package br.com.telefonica.controllers;

import com.google.common.collect.Lists;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.webservicescommons.dto.error.ErrorListWsDTO;
import de.hybris.platform.webservicescommons.dto.error.ErrorWsDTO;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.mapping.FieldSetLevelHelper;
import de.hybris.platform.webservicescommons.util.YSanitizer;
import org.apache.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.annotation.Resource;


public class BaseController
{
    private static final Logger LOG = Logger.getLogger(BaseController.class);
    protected static final String DEFAULT_FIELD_SET = FieldSetLevelHelper.DEFAULT_LEVEL;
    protected static final String FULL_FIELD_SET = FieldSetLevelHelper.FULL_LEVEL;
    private DataMapper dataMapper;
    private I18NService i18nService;
    private MessageSource messageSource;

    protected String sanitize(final String input)
    {
        return YSanitizer.sanitize(input);
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler(
            { ModelNotFoundException.class })
    public ErrorListWsDTO handleModelNotFoundException(final Exception ex)
    {
        LOG.info("Handling Exception for this request - " + ex.getClass().getSimpleName() + " - " + sanitize(ex.getMessage()));
        if (LOG.isDebugEnabled())
        {
            LOG.debug(ex);
        }

        return handleErrorInternal(UnknownIdentifierException.class.getSimpleName(), ex.getMessage());
    }

    protected void validate(final Object object, final String objectName, final Validator validator)
    {
        final Errors errors = new BeanPropertyBindingResult(object, objectName);
        validator.validate(object, errors);
        if (errors.hasErrors())
        {
            throw new WebserviceValidationException(errors);
        }
    }

    protected ErrorListWsDTO handleErrorInternal(final String type, final String message)
    {
        final ErrorListWsDTO errorListDto = new ErrorListWsDTO();
        final ErrorWsDTO error = new ErrorWsDTO();
        error.setType(type.replace("Exception", "Error"));
        error.setMessage(sanitize(message));
        errorListDto.setErrors(Lists.newArrayList(error));
        return errorListDto;
    }


    protected DataMapper getDataMapper()
    {
        return dataMapper;
    }

    @Resource(name = "dataMapper")
    public void setDataMapper(final DataMapper dataMapper)
    {
        this.dataMapper = dataMapper;
    }


    protected I18NService getI18nService()
    {
        return i18nService;
    }

    @Resource(name = "i18nService")
    public void setI18nService(final I18NService i18nService)
    {
        this.i18nService = i18nService;
    }

    protected MessageSource getMessageSource()
    {
        return messageSource;
    }

    @Resource(name = "messageSource")
    public void setMessageSource(final MessageSource messageSource)
    {
        this.messageSource = messageSource;
    }
}
