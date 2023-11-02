package org.paccounts.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.paccounts.component.ModelAndViewPopulator;
import org.paccounts.config.CollectionsFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@ControllerAdvice
public class GlobalControllerAdvice extends ResponseEntityExceptionHandler {

    private final ModelAndViewPopulator modelAndViewPopulator;
    private final CollectionsFactory collectionsFactory;

    public GlobalControllerAdvice(ModelAndViewPopulator modelAndViewPopulator, CollectionsFactory collectionsFactory) {
        this.modelAndViewPopulator = modelAndViewPopulator;
        this.collectionsFactory = collectionsFactory;
    }

    @ExceptionHandler(value = {NoSuchElementException.class})
    protected ModelAndView handleException(HttpServletRequest request) {
        return modelAndViewPopulator.fillError404(request, new ModelAndView());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    Map<String, Object> onConstraintValidationException(ConstraintViolationException ex) {

        List<String> errors = new ArrayList<>();
        errors.add(ex.getLocalizedMessage());

        Map<String, Object> restResponseContainer = collectionsFactory.getRestResponseCollection();
        restResponseContainer.put("errors", errors);

        return restResponseContainer;
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        List<String> errors = new ArrayList<>();
        errors.add(ex.getLocalizedMessage());

        Map<String, Object> restResponseContainer = collectionsFactory.getRestResponseCollection();
        restResponseContainer.put("errors", errors);

        return new ResponseEntity<>(restResponseContainer, HttpStatus.NOT_ACCEPTABLE);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        Map<String, List<String>> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            List<String> fieldErrors = errors.getOrDefault(fieldName, new ArrayList<>());
            fieldErrors.add(error.getDefaultMessage());

            // Remove unacceptable symbols from field name.
            errors.put(normalizeString(fieldName), fieldErrors);
        });

        Map<String, Object> restResponseContainer = collectionsFactory.getRestResponseCollection();
        restResponseContainer.put("errors", errors);

        return new ResponseEntity<>(restResponseContainer, HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(value = {IllegalArgumentException.class, IncompatibleParametersException.class})
    protected ResponseEntity<Object> handleException(RuntimeException exception, WebRequest request) {
        String responseBody = "Exception!";
        return handleExceptionInternal(exception, responseBody, new HttpHeaders(), HttpStatus.CONFLICT, request);
    }

    private String normalizeString(String str) {
        return str.replaceAll("\\[+\\]+\\.+", "_");
    }
}
