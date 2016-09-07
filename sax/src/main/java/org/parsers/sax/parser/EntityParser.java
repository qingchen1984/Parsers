package org.parsers.sax.parser;

import org.json.simple.JSONArray;
import org.parsers.commons.exceptions.ParserException;
import org.parsers.sax.beans.EntityBean;
import org.parsers.sax.handler.EntityHandler;

import java.io.InputStream;
import java.util.List;

public interface EntityParser {

    enum WHITE_LIST {

        AUTONOMY("http://IamWhitelisted.com");

        private final String entity;
        WHITE_LIST(final String entity){
            this.entity = entity;
        }
        @Override
        public String toString(){
            return this.entity;
        }
    }

    List<EntityBean> parseEntities(EntityHandler entityHandler) throws ParserException;
    JSONArray processEntitiesAsJson() throws ParserException;
    InputStream getInput();

}
