package org.parsers.sax.parser;

import org.json.simple.JSONArray;
import org.parsers.commons.exceptions.ParserException;
import org.parsers.sax.beans.EntityBean;
import org.parsers.sax.handler.EntityHandler;

import java.util.List;

abstract class AbstractEntityParser implements EntityParser{

    @Override
    public JSONArray processEntitiesAsJson() throws ParserException{
        final JSONArray jsonArray = new JSONArray();
        final List<EntityBean> bulletinList = this.parseEntities(new EntityHandler());
        jsonArray.addAll(bulletinList);
        return jsonArray;
    }

}