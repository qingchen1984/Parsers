package org.parsers.sax.handler;

import org.parsers.commons.GlobalConstants;
import org.parsers.sax.beans.EntityBean;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class EntityHandler extends DefaultHandler {

    private Boolean documentAvailable = false;
    private Boolean shouldCreateBuffer = false;
    static final String DREREFERENCE_TAG = "DREREFERENCE";
    static final String CREATE_DATE_TAG = "CREATE_DATE";
    static final String TITLE_TAG = "TITLE";
    static final String RESULTSURL_TAG = "RESULTSURL";
    static final String DOCUMENT_TAG = "DOCUMENT";
    private static final String TITLE_HEAD_REPLACEMENT_PATTERN = "(?i)" +
            "(^(intel\\ssecurity\\s-\\ssecurity\\sbulletin|" +
            "mcafee\\ssecurity\\sbulletin)" +
            "(:|\\s(-|\\u2013))\\s)";
    private static final String TITLE_TAIL_REPLACEMENT_PATTERN = "(?i)(\\s\\(sb[0-9]{5}\\)$)";
    private static final String SUPPORTED_SINGLE_QUOTE_UNICODE = "\u2019";

    private static final String DATE_FORMAT = "dd-MMM-yyyy";
    private static final String TIMEZONE = "GMT";
    private static final String DREREFERENCE_FILTER = "_en_US";

    private final StringBuilder elementBuffer = new StringBuilder();
    private final List<EntityBean> bulletinList = new ArrayList<>();
    private static Map<String, TagHandler> tagToFunction = new ConcurrentHashMap<>();

    private int tagsProcessed;
    private final int tagsToBeProcessed;
    private String titleValue;
    private String dreReferenceValue;
    private String createDateValue;
    private String resultsUrlValue;

    public EntityHandler(){
        super();
        tagToFunction.put(TITLE_TAG, (String tagValue) ->
                titleValue = filterTitles(tagValue));

        tagToFunction.put(DREREFERENCE_TAG, (String tagValue) ->
                dreReferenceValue = tagValue.replace(DREREFERENCE_FILTER, GlobalConstants.EMPTY_TEXT));

        tagToFunction.put(CREATE_DATE_TAG, (String tagValue) ->
                createDateValue = convertEpochInGMT(tagValue));

        tagToFunction.put(RESULTSURL_TAG, (String tagValue) ->
                resultsUrlValue = tagValue);
        tagsToBeProcessed = tagToFunction.size();
    }

    private void resetValues(){
        titleValue = GlobalConstants.EMPTY_TEXT;
        createDateValue = GlobalConstants.EMPTY_TEXT;
        dreReferenceValue = GlobalConstants.EMPTY_TEXT;
        resultsUrlValue = GlobalConstants.EMPTY_TEXT;
        tagsProcessed = 0;
    }

    private static String filterTitles(String title){
        return title.replaceFirst(TITLE_HEAD_REPLACEMENT_PATTERN, GlobalConstants.EMPTY_TEXT)
                .replaceFirst(TITLE_TAIL_REPLACEMENT_PATTERN, GlobalConstants.EMPTY_TEXT)
                .replaceAll("\'", SUPPORTED_SINGLE_QUOTE_UNICODE).trim();
    }

    private static String convertEpochInGMT(String epoch){
        final SimpleDateFormat dateFormatGMT = new SimpleDateFormat(DATE_FORMAT, Locale.US);
        dateFormatGMT.setTimeZone(TimeZone.getTimeZone(TIMEZONE));
        return dateFormatGMT.format( new Date(Long.parseLong(epoch)) );
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        elementBuffer.setLength(0);
        if(DOCUMENT_TAG.equals(qName)) {
            documentAvailable = true;
        } else if(DREREFERENCE_TAG.equals(qName) || TITLE_TAG.equals(qName) ||
                RESULTSURL_TAG.equals(qName) || CREATE_DATE_TAG.equals(qName)){
            if(!documentAvailable){
                throw new SAXException("Found entity " + qName + " out of order in the xml");
            }
            tagsProcessed++;
            shouldCreateBuffer = true;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        super.characters(ch, start, length);
        if(shouldCreateBuffer){
            elementBuffer.append(new String(ch, start, length).trim());
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);
        final TagHandler tagHandler = tagToFunction.get(qName);

        if(DOCUMENT_TAG.equals(qName)){
            documentAvailable = false;
        } else if(DREREFERENCE_TAG.equals(qName) || TITLE_TAG.equals(qName) ||
                RESULTSURL_TAG.equals(qName) || CREATE_DATE_TAG.equals(qName)){
            tagHandler.handleTag(elementBuffer.toString());
            shouldCreateBuffer = false;
        }
        if(tagsProcessed == tagsToBeProcessed) {
            final EntityBean bulletin = new EntityBean(titleValue, resultsUrlValue,
                    createDateValue, dreReferenceValue);
            resetValues();
            bulletinList.add(bulletin);
        }
    }

    public List<EntityBean> getBulletinList() {
        return bulletinList;
    }

}
