package io.github.exampleuser.exampleplugin.messenger.exception;

import java.io.Serial;

/**
 * Messenger initialization exception is thrown during message broker initialization.
 */
public class MessengerInitializationException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Instantiates a new Messenger initialization exception.
     *
     * @param t the throwable
     */
    public MessengerInitializationException(Throwable t) {
        super(t);
    }

    /**
     * Instantiates a new Messenger initialization exception.
     *
     * @param s the message
     * @param t the throwable
     */
    public MessengerInitializationException(String s, Throwable t) {
        super(s, t);
    }

    /**
     * Instantiates a new Messenger initialization exception.
     *
     * @param s the message
     */
    public MessengerInitializationException(String s) {
        super(s);
    }
}
