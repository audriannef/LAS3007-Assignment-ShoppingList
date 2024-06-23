package com.shoppingList.restservice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.shoppingList.restservice.exceptions.ResourceAlreadyExistsException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.MethodArgumentNotValidException;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(ResourceAlreadyExistsException.class)
	protected ResponseEntity<Object> handleResourceAlreadyExistsException(ResourceAlreadyExistsException ex,
			WebRequest request) {
		return ResponseEntity.status(HttpStatus.CONFLICT).body("Resource already exists");
	}
	
	@Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String bodyOfResponse = ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        return new ResponseEntity<Object>(bodyOfResponse, HttpStatus.BAD_REQUEST);
    }
}