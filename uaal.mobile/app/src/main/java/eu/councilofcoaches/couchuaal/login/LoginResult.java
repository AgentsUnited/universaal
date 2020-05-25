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
package eu.councilofcoaches.couchuaal.login;

import androidx.annotation.Nullable;

/**
 * Authentication result : success (user details) or error message.
 */
class LoginResult {
    @Nullable
    private String success;
    @Nullable
    private Integer error;

    LoginResult(@Nullable Integer error) {
        this.error = error;
    }

    LoginResult(@Nullable String success) {
        this.success = success;
    }

    @Nullable
    String getSuccess() {
        return success;
    }

    @Nullable
    Integer getError() {
        return error;
    }
}
