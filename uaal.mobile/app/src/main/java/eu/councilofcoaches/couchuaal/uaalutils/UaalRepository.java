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

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
public class UaalRepository {

    private static volatile UaalRepository instance;
    private final SharedPreferences sharedPref; // Maybe I am just masking the context leak...
    private UaalDataSource dataSource;
    public static final String KEY_VALID_USR = "setting_connusr_key";
    public static final String KEY_VALID_PWD = "setting_connpwd_key";
    private static final String DEFAULT_USR = "couch";
    private static final String DEFAULT_PWD = "couch";
    private static final String KEY_URL = "setting_connurl_key";
    private static final String DEFAULT_URL = "http://130.89.78.74:9000/uaal";

    // If user credentials will be cached in local storage, it is recommended it be encrypted
    // @see https://developer.android.com/training/articles/keystore
    // @see https://developer.android.com/topic/security/data.md

    // private constructor : singleton access
    private UaalRepository(Context context) {
        dataSource = new UaalDataSource();
        sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static UaalRepository getInstance(Context context) {
        if (instance == null) {
            instance = new UaalRepository(context);
        }
        return instance;
    }

    public boolean isLoggedIn() {
        // A user is logged in if we have saved a server-validated usr & pwd
        return (sharedPref.contains(KEY_VALID_USR) && sharedPref.contains(KEY_VALID_PWD));
    }

    public void logout() {
        dataSource.logout(
                sharedPref.getString(KEY_URL, DEFAULT_URL),
                sharedPref.getString(KEY_VALID_USR, DEFAULT_USR),
                sharedPref.getString(KEY_VALID_PWD, DEFAULT_PWD));
        sharedPref.edit()
                .remove(KEY_VALID_USR)
                .remove(KEY_VALID_PWD)
                .apply();
    }

    public UaalResult login(String username, String password) {
        // handle login
        UaalResult result = dataSource.login(
                sharedPref.getString(KEY_URL, DEFAULT_URL),
                username,
                password);
        if (result instanceof UaalResult.Success) {
            sharedPref.edit()
                    .putString(KEY_VALID_USR,username)
                    .putString(KEY_VALID_PWD,password)
                    .apply();
        }
        return result;
    }

    public UaalResult sendEvent(String body){
        return dataSource.sendEvent(
                sharedPref.getString(KEY_URL, DEFAULT_URL),
                sharedPref.getString(KEY_VALID_USR, DEFAULT_USR),
                sharedPref.getString(KEY_VALID_PWD, DEFAULT_PWD),
                body);
    }

}
