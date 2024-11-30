package com.producer.errors;

import org.apache.avro.AvroTypeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ExceptionsHandler {

    /**
     * Handle SchemaRegistryException (schema not found ...etc)
     *
     * @param ex
     *
     * @return
     */
    @ExceptionHandler({ SchemaRegistryException.class, AvroTypeException.class})
    public ResponseEntity<Object> handleSchemaRegistryException(Exception ex) {

        Map<String, String> errors = new HashMap<>();
        errors.put("Message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
}
