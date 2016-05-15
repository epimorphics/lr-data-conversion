package com.epimorphics.lr.data;

/**
 * Report errors
 * 
 * @author bwm
 *
 */
public class ErrorHandler {
    public void reportError(String message, Throwable t) {
    	reportError(message);
    	System.out.println(t);
    	System.err.println(t.getMessage());
    }
    
    public void reportError(String message) {
    	System.err.println("ERROR: " + message);
    }
    
    public void warn(String message) {
    	System.err.println("WARNING: " + message);
    }
}
