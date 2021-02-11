package org.wingsofcarolina.gs.common;

public class APIError {

    private String message;

    public APIError() { }

    public APIError(String message) {
    		this.message = message;
    }
    
    public String message() {
        return message;
    }
}
