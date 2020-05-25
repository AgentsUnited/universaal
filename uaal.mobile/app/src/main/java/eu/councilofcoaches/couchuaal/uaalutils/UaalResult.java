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

/**
 * A generic class that holds a result success w/ data or an error exception.
 */
public class UaalResult<T> {
    // hide the private constructor to limit subclass types (Success, Error)
    private UaalResult() {
    }

    @Override
    public String toString() {
        if (this instanceof UaalResult.Success) {
            UaalResult.Success success = (UaalResult.Success) this;
            return "Success[data=" + success.getData().toString() + "]";
        } else if (this instanceof UaalResult.Error) {
            UaalResult.Error error = (UaalResult.Error) this;
            return "Error[string code=" + error.getError().toString() + "]";
        }
        return "";
    }

    // Success sub-class
    public final static class Success<T> extends UaalResult {
        private T data;

        public Success(T data) {
            this.data = data;
        }

        public T getData() {
            return this.data;
        }
    }

    // Error sub-class
    public final static class Error extends UaalResult {
        private Integer error;

        public Error(Integer error) {
            this.error = error;
        }

        public Integer getError() {
            return this.error;
        }
    }
}
