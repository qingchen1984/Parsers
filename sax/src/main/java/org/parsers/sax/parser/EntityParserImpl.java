package org.parsers.sax.parser;

import org.parsers.commons.exceptions.ParserException;
import org.parsers.commons.exceptions.ParserExceptionFactory;
import org.parsers.sax.beans.EntityBean;
import org.parsers.sax.handler.EntityHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class EntityParserImpl extends AbstractEntityParser{

    private InputStream input;
    private final HttpURLConnection connection;
    private final SAXParserFactory parserFactory = SAXParserFactory.newInstance();
    private static final Logger LOGGER = LoggerFactory.getLogger(EntityParserImpl.class);

    public EntityParserImpl(URL endPointUrl) throws ParserException{
        super();
        try {
            connection = (HttpURLConnection) endPointUrl.openConnection();
            connection.setRequestMethod("GET");
            this.input = connection.getInputStream();
        } catch(IOException e){
            LOGGER.error("Unable to instantiate Bulletin Parser properly :: ", e);
            throw ParserExceptionFactory.create(e);
        }
    }

    private EntityResolver handleExternalEntities(){
        return (String publicId, String systemId) -> {
            for(final WHITE_LIST entity : WHITE_LIST.values()) {
                if (systemId.startsWith(entity.toString())) {
                    return new InputSource(systemId);
                }
            }
            LOGGER.debug("XXE disabled for entity other than the ones white listed.");
            return new InputSource();
        };
    }

    @Override
    public List<EntityBean> parseEntities(EntityHandler bulletinHandler)
            throws ParserException {
        try {
            if(this.input == null) {
                throw new ParserException("InputStream returned null from the requested resource.");
            }else {
                final SAXParser parser = parserFactory.newSAXParser();
                final XMLReader xmlReader = parser.getXMLReader();
                xmlReader.setEntityResolver(handleExternalEntities());
                xmlReader.setContentHandler(bulletinHandler);
                final InputSource inputSource = new InputSource(this.input);
                xmlReader.parse(inputSource);
            }
        } catch(ParserConfigurationException | SAXException | IOException e){
            throw ParserExceptionFactory.create(e);
        } finally {
            try {
                if(this.input != null) {
                    this.input.close();
                }
                if(connection != null){
                    this.connection.disconnect();
                }
            } catch(IOException e){
                LOGGER.error("Unable to close connection : ", e);
            }
        }
        return bulletinHandler.getEntityList();
    }

    @Override
    public InputStream getInput() {
        return input;
    }
}
