package org.parsers.sax.handler;

@FunctionalInterface
interface TagHandler {
    void handleTag(String tagValue);
}
