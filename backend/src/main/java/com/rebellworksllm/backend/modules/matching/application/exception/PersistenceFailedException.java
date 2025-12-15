
package com.rebellworksllm.backend.modules.matching.application.exception;

public class PersistenceFailedException extends WorkflowException {

    public PersistenceFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}