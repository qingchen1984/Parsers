package org.parsers.commons.exceptions;

public class ParserExceptionFactory {

    private ParserExceptionFactory() {}

    public static ParserException create(Throwable x) {
        if(x instanceof ParserException){
            return (ParserException)x;
        }
        return new ParserException(x);
    }
}
