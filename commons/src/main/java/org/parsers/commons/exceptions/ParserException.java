package org.parsers.commons.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParserException extends Exception {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(ParserException.class);

    public ParserException(String errorMessage) {
        super(errorMessage);
        LOG.error(errorMessage);
    }

    public ParserException(Throwable ex) {
        super(ex);
        LOG.error("Error: ", ex);
    }

    public ParserException(String errorMessage, Throwable ex) {
        super(errorMessage, ex);
        LOG.error("Error: " + errorMessage, ex);
    }
}
