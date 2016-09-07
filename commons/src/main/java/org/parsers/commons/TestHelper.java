package org.parsers.commons;

import java.io.IOException;
import java.net.*;

public class TestHelper {

    public static URL getRealUrl(final HttpURLConnection connectionMock){
        final URLStreamHandler customUrlHandler = new URLStreamHandler() {
            @Override
            protected URLConnection openConnection(URL url) throws IOException {
                return connectionMock;
            }
        };
        URL url = null;
        try {
            url = new URL("CanNotMockURL", "SoExtending", 1, "UrlConnection", customUrlHandler);
        } catch(MalformedURLException mue){
            mue.printStackTrace();
        }
        return url;
    }

}
