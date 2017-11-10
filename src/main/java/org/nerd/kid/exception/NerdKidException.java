package org.nerd.kid.exception;

public class NerdKidException extends RuntimeException {
    public NerdKidException() {super();}
    public NerdKidException(String message) {super(message);}
    public NerdKidException(Throwable problem) {super(problem);}
    public NerdKidException(String message, Throwable problem) {super(message, problem);}
}
