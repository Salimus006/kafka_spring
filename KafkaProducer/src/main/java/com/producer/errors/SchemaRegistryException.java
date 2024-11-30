package com.producer.errors;

public class SchemaRegistryException extends RuntimeException{
    public SchemaRegistryException() {
    }

    public SchemaRegistryException(String message) {
        super(message);
    }
}
