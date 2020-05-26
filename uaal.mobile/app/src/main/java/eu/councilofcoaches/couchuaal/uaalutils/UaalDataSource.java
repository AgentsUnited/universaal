/*
Copyright 2020 SABIEN - Universitat Politecnica de Valencia.
This file is part of Agents United Bluetooth Sensors App

Agents United Bluetooth Sensors App is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Agents United Bluetooth Sensors App is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with Agents United Bluetooth Sensors App.  If not, see <https://www.gnu.org/licenses/>.
 */
package eu.councilofcoaches.couchuaal.uaalutils;

import android.util.Log;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import eu.councilofcoaches.couchuaal.R;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class UaalDataSource {
    private static final String TAG = "LoginDataSource";
    private static final String REPLACE_PARAM = "$%p";
    private static final String REPLACE_ID = "$%i";
    private static final String POST_SPACES = "{\r\n"
            + "   \"space\": {\r\n"
            + "     \"@id\": \"" + REPLACE_ID + "\",\r\n"
            + "     \"callback\": \"\"\r\n"
            + "   }\r\n"
            + " }";
    private static final String POST_SPACES_S_CONTEXT_PUBLISHERS = "{\r\n"
            + "  \"publisher\": {\r\n"
            + "    \"@id\": \"" + REPLACE_ID + "\",\r\n"
            + "    \"providerinfo\": \"" + REPLACE_PARAM + "\"\r\n"
            + "  } \r\n"
            + " }";
    private static final String BODY_DEFAULT_PUBLISHER = "@prefix owl: <http://www.w3.org/2002/07/owl#> .\\r\\n" +
            "@prefix : <http://ontology.universAAL.org/Context.owl#> .\\r\\n" +
            "<http://ontology.universAAL.org/uAAL.owl#DefaultContextPublisher"+REPLACE_ID+"> :myClassesOfEvents (\\r\\n" +
            "    [\\r\\n" +
            "      a :ContextEventPattern \\r\\n" +
            "    ]\\r\\n" +
            "  ) ;\\r\\n" +
            "  a :ContextProvider ;\\r\\n" +
            "  :hasType :controller .\\r\\n" +
            ":controller a :ContextProviderType .";

    public UaalResult login(String url, String username, String password) {
        try {
            // First try creating a space
            String json = POST_SPACES
                    .replace(REPLACE_ID, username) //TODO Use AP name as ID, also as viewable name
                    .replace(REPLACE_PARAM, "");
            int result = sendHTTP("POST", url + "/spaces/", json, username, password, false);
            if (result < 0) {
                return new UaalResult.Error(R.string.login_failed_connection);
            }else if ((result < 200 || result > 299) && result != 409) { // ignore 409 conflict (already created)
                return new UaalResult.Error(R.string.login_failed_invalid);
            }
            // Then try creating the publisher
            json = POST_SPACES_S_CONTEXT_PUBLISHERS
                    .replace(REPLACE_ID, "default")
                    .replace(REPLACE_PARAM, BODY_DEFAULT_PUBLISHER.replace(REPLACE_ID,username));
            result = sendHTTP("POST", url + "/spaces/" + username + "/context/publishers", json, username, password, false);
            if (result < 0) {
                return new UaalResult.Error(R.string.login_failed_connection);
            }else if ((result < 200 || result > 299) && result != 409) { // ignore 409 conflict (already created)
                return new UaalResult.Error(R.string.login_failed_invalid);
            }
            // If both are OK, auth and register are OK
            return new UaalResult.Success<>(username);
        } catch (Exception e) {
            return new UaalResult.Error(R.string.generic_failed_exception);
        }
    }

    void logout(String url, String username, String password) {
        sendHTTP("DELETE", url + "/spaces/" + username, null, username, password, false);
    }

    UaalResult sendEvent(String url, String username, String password, String body){
        try{
            int result = sendHTTP("POST", url + "/spaces/" + username + "/context/publishers/default", body, username, password, true);
            if ((result < 200 || result > 299)){
                return new UaalResult.Error(R.string.event_failed_connection);
            }
            return new UaalResult.Success<>("");
        } catch (Exception e) {
            return new UaalResult.Error(R.string.generic_failed_exception);
        }
    }

    private static int sendHTTP(String method, String restURL, String body, String user, String pwd, boolean text) {
        HttpURLConnection conn = null;
        try {
            byte[] data = null;
            if (body != null) data = body.getBytes(StandardCharsets.UTF_8);
            String auth = "Basic " + Base64.encodeBytes((user + ":" + pwd).getBytes());
            URL url= new URL(restURL);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(method);
            conn.setInstanceFollowRedirects(false);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setReadTimeout(30000);
            conn.setConnectTimeout(30000);
            conn.setRequestProperty("Content-Type", text?"text/plain":"application/json");
            conn.setRequestProperty("charset", "utf-8");
            if (body != null)
                conn.setRequestProperty("Content-Length", "" + data.length);
            conn.setRequestProperty("Authorization", auth);

            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            if (body != null) wr.write(data);
            wr.flush();
            wr.close();

            Log.d(TAG, "SENT TO SERVER: " + url);
            return conn.getResponseCode();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // close the connection and set all objects to null
            if (conn != null) {
                conn.disconnect();
            }
        }
        return -1;
    }
}
