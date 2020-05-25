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
package eu.councilofcoaches.couchuaal;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import eu.councilofcoaches.couchuaal.login.LoginActivity;
import eu.councilofcoaches.couchuaal.uaalutils.UaalRepository;

/**
 * Created by alfiva on 08/09/2017.
 * .
 */

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
//    private static final int CODE_STORAGE = 1;
    private static final int CODE_BLUETOOTH_BP = 2;
    private static final int CODE_BLUETOOTH_WS = 3;
    private Button mButton1, mButton3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        if(!UaalRepository.getInstance(getApplicationContext()).isLoggedIn()){
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }
        setContentView(R.layout.layout_main);
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        mButton1 = findViewById(R.id.button_bp_651);
        mButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, CODE_BLUETOOTH_BP)){
                    startBTActivity(CODE_BLUETOOTH_BP);
                }
            }
        });
        mButton3 = findViewById(R.id.button_ws_352);
        mButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, CODE_BLUETOOTH_WS)){
                    startBTActivity(CODE_BLUETOOTH_WS);
                }
            }
        });
    }

    // ---PERMISSIONS---

    private boolean checkPermission(String permission, int code){
        Log.d(TAG, "checkPermission");
        if (ContextCompat.checkSelfPermission(getApplicationContext(), permission)
                != PackageManager.PERMISSION_GRANTED) { // Permission is not granted
            ActivityCompat.requestPermissions(this, new String[]{permission}, code);
            return false;
        } else { // Permission has already been granted
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult");
        switch (requestCode) {
//            case CODE_STORAGE:  // If request is cancelled, the result arrays are empty.
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    Intent i=new Intent(getApplicationContext(), org.universAAL.android.receivers.RestartReceiver.class);
//                    i.setAction(AppConstants.ACTION_SYS_START);
//                    sendBroadcast(i); // permission was granted
//                } else {
//                    Toast.makeText(this, R.string.warning_store_not_granted, Toast.LENGTH_LONG).show();
//                    finish(); // permission denied
//                }
//                break;
            case CODE_BLUETOOTH_BP:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startBTActivity(CODE_BLUETOOTH_BP);
                } else {
                    Toast.makeText(this, R.string.warning_bt_not_granted, Toast.LENGTH_LONG).show();
                }
                break;
            case CODE_BLUETOOTH_WS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startBTActivity(CODE_BLUETOOTH_WS);
                } else {
                    Toast.makeText(this, R.string.warning_bt_not_granted, Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    // ---BLUETOOTH---

    private boolean checkBluetooth(int code) {
        Log.d(TAG, "checkBluetooth"); // BleHelper also checks BT but may fail due to race conds.
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        if (bluetoothManager == null) {
            Log.e(TAG, "This device does not have Bluetooth?");
        } else {
            BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
            if (bluetoothAdapter == null) {
                Log.e(TAG, "This device does not have Bluetooth?");
            } else {
                if (!bluetoothAdapter.isEnabled()) {
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(intent, code);
                }else{
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult");
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != RESULT_OK){
            Toast.makeText(getApplicationContext(), R.string.warning_bt_not_enabled,
                    Toast.LENGTH_LONG).show();
        }else {
            switch (requestCode) {
                case CODE_BLUETOOTH_BP:
                    Intent intent = new Intent(getApplicationContext(), BP651Activity.class);
                    startActivity(intent);
                    break;
                case CODE_BLUETOOTH_WS:
                    intent = new Intent(getApplicationContext(), WS352Activity.class);
                    startActivity(intent);
                    break;
            }
        }
    }

    private void startBTActivity(int code){
        Log.d(TAG, "startBTActivity");
        switch (code){
            case CODE_BLUETOOTH_BP:
                if(checkBluetooth(CODE_BLUETOOTH_BP)){
                    Intent intent = new Intent(getApplicationContext(), BP651Activity.class);
                    startActivity(intent);
                }
                break;
            case CODE_BLUETOOTH_WS:
                if(checkBluetooth(CODE_BLUETOOTH_WS)){
                    Intent intent = new Intent(getApplicationContext(), WS352Activity.class);
                    startActivity(intent);
                }
                break;
        }
    }

    // ---MENU---

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(R.id.action_settings==item.getItemId()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.layout_pin, null);
            final EditText pin = view.findViewById(R.id.editPin);
            builder.setView(view).setTitle(R.string.pinTitle)
                    .setPositiveButton(R.string.pinOk, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            String editable = pin.getText().toString();
                            if (editable.equals("8225")) {
                                Intent i = new Intent(getApplicationContext(),
                                        SettingsActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivity(i);
                            } else {
                                Toast.makeText(getApplicationContext(), R.string.pinError,
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    }).create().show();
        }else if(R.id.action_settings_user==item.getItemId()) {
            mButton1.setEnabled(false);
            mButton3.setEnabled(false);
            new UserLogoutTask().execute();
        }
        return true;
    }

    // ---LOGOUT---

    public class UserLogoutTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            UaalRepository.getInstance(getApplicationContext()).logout(); // Log out current user
            return null;
        }

        @Override
        protected void onPostExecute(Boolean params) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class)); // Login again
            finish();
        }
    }

}
