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

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import java.util.Calendar;
import java.util.Locale;

import jp.co.aandd.bleSimpleApp.base.ADGattService;
import jp.co.aandd.bleSimpleApp.gatt.WeightMeasurement;

/**
 * Created by alfiva on 08/09/2017.
 * .
 */

public class WS352Activity extends SensorActivity {

    private TextView mValue;
    private TextView mTime;
    private TextView mUnit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_ws_352);
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        mValue = findViewById(R.id.value);
        mTime = findViewById(R.id.timestamp);
        mUnit = findViewById(R.id.unit);
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WS352Activity.super.onBackPressed();
            }
        });
    }

    @Override
    public void onWeightReceived(Bundle bundle) {
        double weight = bundle.getDouble(ADGattService.KEY_WEIGHT);
        String units = bundle.getString(ADGattService.KEY_UNIT, WeightMeasurement.VALUE_WEIGHT_SCALE_UNITS_LBS);

        int year = bundle.getInt(ADGattService.KEY_YEAR);
        int month = bundle.getInt(ADGattService.KEY_MONTH);
        int day = bundle.getInt(ADGattService.KEY_DAY);
        int hours = bundle.getInt(ADGattService.KEY_HOURS);
        int minutes = bundle.getInt(ADGattService.KEY_MINUTES);
        int seconds = bundle.getInt(ADGattService.KEY_SECONDS);

        mValue.setText(String.format(Locale.getDefault(), "%.1f", weight));
        mUnit.setText(units);
        if(day==0 && hours==0){
            Calendar c = Calendar.getInstance();
            mTime.setText(getResources().getString(R.string.label_timestamp)
                    .replace("%c", String.format(Locale.getDefault(),
                            "%04d-%02d-%02dT%02d:%02d:%02d", c.get(Calendar.YEAR),
                            c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH),
                            c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE),
                            c.get(Calendar.SECOND))));
        }else{
            mTime.setText(getResources().getString(R.string.label_timestamp)
                    .replace("%c", String.format(Locale.getDefault(),
                            "%04d-%02d-%02dT%02d:%02d:%02d", year, month, day, hours, minutes, seconds)));
        }
    }

    @Override
    public void onPressureReceived(Bundle bundle) {
        Toast.makeText(this, R.string.warning_not_a_bp, Toast.LENGTH_LONG).show();
    }

}
