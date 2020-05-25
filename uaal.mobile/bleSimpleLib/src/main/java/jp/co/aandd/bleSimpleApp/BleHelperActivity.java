package jp.co.aandd.bleSimpleApp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import jp.co.aandd.bleSimpleApp.base.ADGattService;
import jp.co.aandd.bleSimpleApp.gatt.ADGattUUID;
import jp.co.aandd.bleSimpleApp.gatt.WeightMeasurement;

public class BleHelperActivity extends AppCompatActivity {

    private static final String TAG = "BleHelperActivity";
    private static final int REQUEST_ENABLE_BLUETOOTH = 1000;
    public String mAddress = null;
    /**
     * Use this ServiceConnection to be notified when BLE Service comes and goes, just in case.
     */
    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "BleService - connected");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "BleService - disconnected");
        }
    };
    /**
     * This Broadcast Receiver will get all data from BLE Service, including the actual measurements.
     */
    private final BroadcastReceiver bleServiceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String type = intent.getExtras().getString(BleHelperService.EXTRA_TYPE);
            String address = intent.getExtras().getString(BleHelperService.EXTRA_ADDRESS);
            if (BleHelperService.TYPE_GATT_CONNECTED.equals(type)) {
                Log.d(TAG, "DashBoard onReceive TYPE_GATT_CONNECTED");
                if (address!=null) mAddress=address;
            } else if (BleHelperService.TYPE_GATT_DISCONNECTED.equals(type)) {
                Log.d(TAG, "DashBoard onReceive TYPE_GATT_DISCONNECTED");
                mAddress=null;
            } else if (BleHelperService.TYPE_GATT_ERROR.equals(type)) {
                Log.d(TAG, "DashBoard onReceive TYPE_GATT_ERROR");
                mAddress=null;
            } else if (BleHelperService.TYPE_GATT_SERVICES_DISCOVERED.equals(type)) {
                Log.d(TAG, "DashBoard onReceive TYPE_GATT_SERVICES_DISCOVERED");
                if (address!=null) mAddress=address;
            } else if (BleHelperService.TYPE_INDICATION_VALUE.equals(type)) {
                Log.d(TAG, "DashBoard onReceive TYPE_INDICATION_VALUE");
                if (address!=null) mAddress=address; //TODO put in bundle
                Bundle bundle = intent.getBundleExtra(BleHelperService.EXTRA_VALUE);
                String uuidString = intent.getExtras().getString(BleHelperService.EXTRA_CHARACTERISTIC_UUID);
                Log.d(TAG, "Parse :" + bundle);
                Log.d(TAG, "UUID :" + uuidString);
                onDataReceived(uuidString, bundle);
            }
        }
    };

    /**
     * If extending this Activity, override onCreate and call super.onCreate(savedInstanceState);
     * first, then set your own UI. Otherwise the activity will show a blank screen with
     * "Awaiting Bluetooth data" on it.
     * @param savedInstanceState See Activity documentation.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "On Create: User Interface");
        LinearLayout lv=new LinearLayout(this);
        TextView tv=new TextView(this);
        tv.setText("Awaiting Bluetooth data");
        lv.addView(tv);
        setContentView(lv);
//        // This was in onResume, but initializeBluetooth may pause activity to enable BT...
//        // This is how you start the BLE Service and register for it.
//        IntentFilter filter = new IntentFilter(BleHelperService.ACTION_BLE_SERVICE);
//        initializeBluetooth();
//        registerReceiver(bleServiceReceiver, filter);
    }

    /**
     * Make sure you start BLE Service in your activity, then close it when appropriate.
     * Here it is started in onResume, so it is stopped in onPause.
     */
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "On Resume: Start BT service");
        // This is how you start the BLE Service and register for it.
        IntentFilter filter = new IntentFilter(BleHelperService.ACTION_BLE_SERVICE);
        initializeBluetooth();
        registerReceiver(bleServiceReceiver, filter);
    }

    /**
     * Make sure you start BLE Service in your activity, then close it when appropriate.
     * Here it is started in onCreate, so it is stopped in onDestroy.
     */
    @Override
    protected void onPause() {
        super.onPause();
//        // ...This was in onPause but service perhaps wasnt ready (dialog to enable BT) and unbind failed
        Log.d(TAG, "On Pause: Stop BT service");
        // This is how you stop the BLE Service and unregister from it.
        unregisterReceiver(bleServiceReceiver);
        Intent intent = new Intent(this, BleHelperService.class);
        unbindService(serviceConnection);
        stopService(intent);
    }

    /**
     * Used to get confirmation from system that the user enabled BT after requesting it.
     * @param requestCode See Activity documentation.
     * @param resultCode See Activity documentation.
     * @param data See Activity documentation.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BLUETOOTH) {
            if (resultCode == RESULT_OK) {
                startBleService();
            } else {
                Log.e(TAG, "BluetoothAdapter cannot be enabled");
            }
        }
    }

    /**
     * Request user to enable BT if it was not enabled, before starting the BLE Service.
     */
    private void initializeBluetooth() {
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        if (bluetoothManager == null) {
            Log.e(TAG, "BluetoothManager does not exist");
        } else {
            BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
            if (bluetoothAdapter == null) {
                Log.e(TAG, "BluetoothManager does not exist");
            } else {
                if (!bluetoothAdapter.isEnabled()) {
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(intent, REQUEST_ENABLE_BLUETOOTH);
                }
            }
            if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
                Log.e(TAG, "BluetoothAdapter cannot be enabled");
            } else {
                startBleService();
            }
        }
    }

    /**
     * Actually start the BLE Service, but make sure BT is enabled first.
     */
    private void startBleService() {
        Intent intent = new Intent(this, BleHelperService.class);
        startService(intent);
        boolean result = bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        if (result) {
            Log.d(TAG, "BleService - success");
        } else {
            Log.e(TAG, "Failed to bind BleService");
        }
    }

    /**
     * Got a measurement from BT. Override this method to handle it yourself.
     * @param characteristicUuidString Identifies the type of measurement: One of the Measurements in ADGattUUID.
     * @param bundle Bundle containing the measurement information.
     */
    public void onDataReceived(String characteristicUuidString, Bundle bundle) {
        if (ADGattUUID.WeightScaleMeasurement.toString().equals(characteristicUuidString) ||
                ADGattUUID.AndCustomWeightScaleMeasurement.toString().equals(characteristicUuidString)) {
            Log.d(TAG, "Received Data WS");
            double weight = bundle.getDouble(ADGattService.KEY_WEIGHT);
            String units = bundle.getString(ADGattService.KEY_UNIT, WeightMeasurement.VALUE_WEIGHT_SCALE_UNITS_LBS);

            int year = bundle.getInt(ADGattService.KEY_YEAR);
            int month = bundle.getInt(ADGattService.KEY_MONTH);
            int day = bundle.getInt(ADGattService.KEY_DAY);
            int hours = bundle.getInt(ADGattService.KEY_HOURS);
            int minutes = bundle.getInt(ADGattService.KEY_MINUTES);
            int seconds = bundle.getInt(ADGattService.KEY_SECONDS);

            String finalValue = String.format("%.1f", weight) + " " + units;
            String finalTimeStamp = String.format("%04d-%02d-%02dT%02d:%02d:%02d", year, month, day, hours, minutes, seconds);
            onWeightReceived(finalValue, finalTimeStamp);
        } else if (ADGattUUID.BloodPressureMeasurement.toString().equals(characteristicUuidString)) {
            Log.d(TAG, "Received Data BP");
            int sys = (int) bundle.getFloat(ADGattService.KEY_SYSTOLIC);
            int dia = (int) bundle.getFloat(ADGattService.KEY_DIASTOLIC);
            int pul = (int) bundle.getFloat(ADGattService.KEY_PULSE_RATE);
            int irregular = (int) bundle.getFloat(ADGattService.KEY_IRREGULAR_PULSE_DETECTION);

            int year = (int) bundle.getInt(ADGattService.KEY_YEAR);
            int month = (int) bundle.getInt(ADGattService.KEY_MONTH);
            int day = (int) bundle.getInt(ADGattService.KEY_DAY);

            int hours = (int) bundle.getInt(ADGattService.KEY_HOURS);
            int minutes = (int) bundle.getInt(ADGattService.KEY_MINUTES);
            int seconds = (int) bundle.getInt(ADGattService.KEY_SECONDS);

            String finalValue = "Systolic: " + sys + " Diastolic: " + dia + " Pulse: " + pul + " Irregularity: " + irregular;
            String finalTimeStamp = String.format("%04d-%02d-%02dT%02d:%02d:%02d", year, month, day, hours, minutes, seconds);
            onBloodPressureReceived(finalValue, finalTimeStamp);
        }
    }

    /**
     * Got a blood pressure measurement. Override to handle the message yourself.
     * @param finalValue String representation of the value: Systolic: ## Diastolic: ## Pulse: ## Irregularity: ##
     * @param finalTimeStamp String representation of the timestamp. Can be used to parse into Date/Calendar. May be 0.
     */
    public void onBloodPressureReceived(String finalValue, String finalTimeStamp) {
        Log.d(TAG, "onBloodPressureReceived:\n"+finalValue+"\n"+finalTimeStamp);
    }

    /**
     * Got a weight measurement. Override to handle the message yourself.
     * @param finalValue String representation of the value: ##.# kg/lb
     * @param finalTimeStamp String representation of the timestamp. Can be used to parse into Date/Calendar. May be 0.
     */
    public void onWeightReceived(String finalValue, String finalTimeStamp) {
        Log.d(TAG, "onWeightReceived:\n"+finalValue+"\n"+finalTimeStamp);
    }
}