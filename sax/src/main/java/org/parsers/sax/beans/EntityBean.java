package org.parsers.sax.beans;

import org.apache.commons.lang.StringEscapeUtils;

public class EntityBean {

    private final String title;
    private final String resultsUrl;
    private final String createDate;
    private final String dreReference;

    public EntityBean(String title, String resultsUrl,
                      String createDate, String dreReference) {

        this.title = title;
        this.resultsUrl = resultsUrl;
        this.dreReference = dreReference;
        this.createDate = createDate;

    }

    @Override
    public String toString() {
        final StringBuilder entity = new StringBuilder(100);
        entity.append("{\"title\":\"").append(StringEscapeUtils.escapeJavaScript(title))
                .append("\", \"resultsUrl\":\"").append(StringEscapeUtils.escapeJavaScript(resultsUrl))
                .append("\", \"createDate\":\"").append(createDate)
                .append("\", \"dreReference\":\"").append(dreReference).append("\"}");
        return entity.toString();
    }
}
