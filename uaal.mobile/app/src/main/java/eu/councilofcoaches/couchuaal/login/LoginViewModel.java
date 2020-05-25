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

import android.app.Application;
import android.os.AsyncTask;
import android.util.Patterns;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import eu.councilofcoaches.couchuaal.R;
import eu.councilofcoaches.couchuaal.uaalutils.UaalRepository;
import eu.councilofcoaches.couchuaal.uaalutils.UaalResult;

public class LoginViewModel extends AndroidViewModel {

    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private UaalRepository loginRepository;

    // This constructor is called by the viewmodelproviders admin
    public LoginViewModel(Application application) {
        super(application);
        this.loginRepository = UaalRepository.getInstance(application.getApplicationContext());
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public void login(String username, String password) {
        // can be launched in a separate asynchronous job
        new UserLoginTask().execute(username, password);
    }

    void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new LoginFormState(R.string.login_invalid_username, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.login_invalid_password));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            return !username.trim().isEmpty();
        }
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<String, Void, UaalResult> {

        @Override
        protected UaalResult doInBackground(String... params) {
            return loginRepository.login(params[0], params[1]);
        }

        @Override
        protected void onPostExecute(final UaalResult result) {
            if (result instanceof UaalResult.Success) {
                String data = ((UaalResult.Success<String>) result).getData();
                loginResult.setValue(new LoginResult(data));
            } else {
                loginResult.setValue(new LoginResult(((UaalResult.Error) result).getError()));
            }
        }

        @Override
        protected void onCancelled() {
            loginResult.setValue(new LoginResult(R.string.login_failed));
        }
    }
}
