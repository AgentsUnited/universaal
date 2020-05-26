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

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import java.util.Locale;

import eu.councilofcoaches.couchuaal.uaalutils.Body;
import eu.councilofcoaches.couchuaal.uaalutils.UaalRepository;
import eu.councilofcoaches.couchuaal.uaalutils.UaalResult;
import eu.councilofcoaches.couchuaal.uaalutils.Base64Binary;
import jp.co.aandd.bleSimpleApp.BleHelperActivity;
import jp.co.aandd.bleSimpleApp.base.ADGattService;
import jp.co.aandd.bleSimpleApp.gatt.ADGattUUID;

/**
 * Created by alfiva on 08/09/2017.
 * .
 */

public abstract class SensorActivity extends BleHelperActivity {

    @SuppressLint("SetTextI18n")
    @Override
    public void onDataReceived(String characteristicUuidString, Bundle bundle) {
        String id = mAddress;//bundle.getString(BleHelperService.EXTRA_ADDRESS);
        if (id != null) {
            id = Body.PREFIX_SENSOR + id.replace(":", "");
        }
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String unit = sharedPref.getString("pref_unit", "");
        String user = sharedPref.getString(UaalRepository.KEY_VALID_USR, "alfiva@itaca.upv.es");
        String pass = sharedPref.getString(UaalRepository.KEY_VALID_PWD, "alvaro@couch");
        if (ADGattUUID.WeightScaleMeasurement.toString().equals(characteristicUuidString) ||
                ADGattUUID.AndCustomWeightScaleMeasurement.toString().equals(characteristicUuidString)) {
            onWeightReceived(bundle);
            double weight = bundle.getDouble(ADGattService.KEY_WEIGHT);

            String wunit = Body.UNIT_NONE;
            if ("KG".equals(unit)) {
                wunit = Body.UNIT_KG;
            } else if ("LB".equals(unit)) {
                wunit = Body.UNIT_LB;
            }
            // UAAL: Send this data as a ContextEvent to uAAL.
            String body = Body.BODY_EVENT_WEIGHT.replace(Body.K_THINGID, id != null ? id : Body.DEFAULT_WID)
                    .replace(Body.K_WMEASUREMENT, Body.PREFIX_WID + System.currentTimeMillis())
                    .replace(Body.K_WVALUE, String.format(Locale.getDefault(), "%.1f", weight).replaceAll("[',]", "."))
                    .replace(Body.K_USERNAME, user)
                    .replace(Body.K_ENCODED, new Base64Binary(pass.getBytes()).toString())
                    .replace(Body.K_EVENTID, Body.makeEventID())
                    .replace(Body.K_WEIGHTUNIT, wunit)
                    .replace(Body.K_TIMESTAMP, Long.toString(System.currentTimeMillis()));

            new EventTask().execute(body);
        } else if (ADGattUUID.BloodPressureMeasurement.toString().equals(characteristicUuidString)) {
            onPressureReceived(bundle);
            Float sys = bundle.getFloat(ADGattService.KEY_SYSTOLIC);
            Float dia = bundle.getFloat(ADGattService.KEY_DIASTOLIC);
            int pul = (int) bundle.getFloat(ADGattService.KEY_PULSE_RATE);

            // UAAL: Send this data as a ContextEvent to uAAL.
            String body = Body.BODY_EVENT_BP_HR.replace(Body.K_THINGID, id != null ? id : Body.DEFAULT_BPID)
                    .replace(Body.K_BPMEASUREMENT, Body.PREFIX_BPID + System.currentTimeMillis())
                    .replace(Body.K_HRMEASUREMENT, Body.PREFIX_HRID + System.currentTimeMillis())
                    .replace(Body.K_BPSYSVALUE, String.format(Locale.getDefault(), "%.1f", sys).replaceAll("[',]", "."))
                    .replace(Body.K_BPDIASVALUE, String.format(Locale.getDefault(), "%.1f", dia).replaceAll("[',]", "."))
                    .replace(Body.K_HRVALUE, Integer.toString(pul))
                    .replace(Body.K_USERNAME, user)
                    .replace(Body.K_ENCODED, new Base64Binary(pass.getBytes()).toString())
                    .replace(Body.K_EVENTID, Body.makeEventID())
                    .replace(Body.K_TIMESTAMP, Long.toString(System.currentTimeMillis()));
            new EventTask().execute(body);

        } else {
            Toast.makeText(this, R.string.error_reading_value, Toast.LENGTH_LONG).show();
        }
    }

    public abstract void onWeightReceived(Bundle bundle);

    public abstract void onPressureReceived(Bundle bundle);

    public class EventTask extends AsyncTask<String, Void, UaalResult> {

        @Override
        protected UaalResult doInBackground(String... params) {
            return UaalRepository.getInstance(getApplicationContext()).sendEvent(params[0]);
        }

        @Override
        protected void onPostExecute(final UaalResult result) {
            if (result instanceof UaalResult.Error) {
                Toast.makeText(getApplicationContext(), ((UaalResult.Error) result).getError(), Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            Toast.makeText(getApplicationContext(), R.string.error_sending_value, Toast.LENGTH_LONG).show();
        }
    }
}
