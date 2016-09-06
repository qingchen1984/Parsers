package org.parsers.sax;

import org.json.simple.JSONArray;

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

    //List<EntityBean> parseEntities(BulletinHandler bulletinHandler) throws ParseException;
    //JSONArray processBulletinsAsJson() throws ParseException;
    //InputStream getInput();

}
