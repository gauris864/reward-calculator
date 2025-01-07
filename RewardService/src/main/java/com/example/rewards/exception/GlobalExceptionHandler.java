package com.example.rewards.exception;

import java.time.LocalDate;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(DateFormatException.class)
	public ResponseEntity<ErrorResponse> handleDateFormatException(DateFormatException ex) {
		ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
				ex.getMessage(), LocalDate.now(), "Please ensure the date format is 'yyyy-MM-dd'.");

		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}

    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),            
                LocalDate.now(),
                String.format("The requested %s with ID %s could not be found.", ex.getResourceType(), ex.getResourceId())
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
    
   @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                ex.getMessage(),                          
                LocalDate.now(),                     
                "An unexpected error occurred"            
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
