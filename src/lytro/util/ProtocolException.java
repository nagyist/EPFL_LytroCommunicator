/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package lytro.util;

/**
 */
public class ProtocolException extends Exception {

    /**
     * Creates a new instance of <code>ProtocolException</code> without detail
     * message.
     */
    public ProtocolException() {
    }

    /**
     * Constructs an instance of <code>ProtocolException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public ProtocolException(String msg) {
        super(msg);
    }

    public ProtocolException(Throwable cause) {
        super(cause);
    }
    
    
}
