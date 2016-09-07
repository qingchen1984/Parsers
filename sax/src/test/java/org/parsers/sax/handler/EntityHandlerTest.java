package org.parsers.sax.handler;

import org.junit.Before;
import org.junit.Test;
import org.parsers.commons.GlobalConstants;
import org.parsers.sax.beans.EntityBean;
import org.xml.sax.SAXException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class EntityHandlerTest {

    private static final String reference = "TAIL10116_en_US";
    private static final String createDate = "1430110880000";
    private static final String resultsUrl = "/index?page=content&id=TAIL10116";
    public static final String title = "title1 replacement - common1 common2:" +
            " Message for title1 (TAIL10116)";

    private static final String reference2 = "TAIL10115_en_US";
    private static final String createDate2 = "1429981765000";
    private static final String resultsUrl2 = "/index?page=content&id=TAIL10115";
    private static final String title2 = "title2 common1 common2 \u2013 Message for title2 (TAIL10111)";

    private static final String expectedEntity = "{" +
            "\"title\":\"Message for title1\"," +
            " \"resultsUrl\":\"\\/index?page=content&id=TAIL10116\"," +
            " \"createDate\":\"27-Apr-2015\"," +
            " \"dreReference\":\"TAIL10116\"}";

    private static final String expectedEntity2 = "{" +
            "\"title\":\"Message for title2\"," +
            " \"resultsUrl\":\"\\/index?page=content&id=TAIL10115\"," +
            " \"createDate\":\"25-Apr-2015\"," +
            " \"dreReference\":\"TAIL10115\"}";

    private static final String titleQuoted = "title2 common1 common2 -" +
            " Quoted scenario \"vulnerabilities\" including \'TEST-2010-1349\' (BIND) (TAIL10116)";

    private static final String expectedEntityEscaped = "{" +
            "\"title\":\"Quoted scenario \\\"vulnerabilities\\\" including \\u2019TEST-2010-1349\\u2019 (BIND)\"," +
            " \"resultsUrl\":\"\\/index?page=content&id=TAIL10116\"," +
            " \"createDate\":\"27-Apr-2015\"," +
            " \"dreReference\":\"TAIL10116\"}";

    private EntityHandler handler;

    @Before
    public void initHandler(){
        handler = new EntityHandler();
    }

    private void triggerElementEventSequence(EntityHandler handler, String qName,
                                             String tagValue) throws SAXException{

        handler.startElement(GlobalConstants.EMPTY_TEXT, GlobalConstants.EMPTY_TEXT, qName, null);
        handler.characters(tagValue.toCharArray(), 0, tagValue.length());

        handler.endElement(GlobalConstants.EMPTY_TEXT, GlobalConstants.EMPTY_TEXT, qName);
    }

    private void triggerCompleteEntitySequence(EntityHandler handler, String drereference,
                                                 String createDate, String resultsUrl,
                                                 String title) throws SAXException{

        handler.startElement(GlobalConstants.EMPTY_TEXT, GlobalConstants.EMPTY_TEXT, EntityHandler.DOCUMENT_TAG, null);
        triggerElementEventSequence(handler, EntityHandler.REFERENCE_TAG, drereference);
        triggerElementEventSequence(handler, EntityHandler.CREATE_DATE_TAG, createDate);
        triggerElementEventSequence(handler, EntityHandler.RESULTSURL_TAG, resultsUrl);
        triggerElementEventSequence(handler, EntityHandler.TITLE_TAG, title);
        handler.endElement(GlobalConstants.EMPTY_TEXT, GlobalConstants.EMPTY_TEXT, EntityHandler.DOCUMENT_TAG);

    }

    @Test
    public void canParseSingleEntity() throws SAXException{

        triggerCompleteEntitySequence(handler, reference, createDate, resultsUrl, title);
        assertNotNull(handler.getEntityList());
        assertEquals(1, handler.getEntityList().size());
        EntityBean entity = handler.getEntityList().get(0);
        assertEquals(expectedEntity, entity.toString());
    }

    @Test
    public void canParseSingleEntityWithEscapedValues() throws SAXException{
        triggerCompleteEntitySequence(handler, reference, createDate, resultsUrl, titleQuoted);
        assertNotNull(handler.getEntityList());
        EntityBean entity = handler.getEntityList().get(0);
        assertEquals(expectedEntityEscaped, entity.toString());
    }

    @Test(expected = SAXException.class)
    public void childFoundWithoutDocumentParent() throws SAXException{
        triggerElementEventSequence(handler, EntityHandler.REFERENCE_TAG, reference);
    }

    @Test
    public void canParseMultipleEntities() throws SAXException{

        triggerCompleteEntitySequence(handler, reference, createDate, resultsUrl, title);
        triggerCompleteEntitySequence(handler, reference2, createDate2, resultsUrl2, title2);

        assertNotNull(handler.getEntityList());
        assertEquals(2, handler.getEntityList().size());

        EntityBean entity = handler.getEntityList().get(0);
        assertNotNull(entity);
        assertEquals(expectedEntity, entity.toString());

        EntityBean entity2 = handler.getEntityList().get(1);
        assertNotNull(entity2);
        assertEquals(expectedEntity2, entity2.toString());
    }
}
