package jp.co.aandd.bleSimpleApp;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.util.Set;
import java.util.UUID;

import jp.co.aandd.bleSimpleApp.gatt.ADGattUUID;
import jp.co.aandd.bleSimpleApp.gatt.AndCustomWeightScaleMeasurement;
import jp.co.aandd.bleSimpleApp.gatt.BloodPressureMeasurement;
import jp.co.aandd.bleSimpleApp.gatt.WeightMeasurement;

public class BleHelperService extends Service implements
        BluetoothAdapter.LeScanCallback {

    public static final String ACTION_BLE_SERVICE = "jp.co.aandd.andblelink.ble.BLE_SERVICE";
    public static final String TYPE_GATT_CONNECTED = "Connected device";
    public static final String TYPE_GATT_DISCONNECTED = "Disconnected device";
    public static final String TYPE_GATT_ERROR = "Gatt Error";
    public static final String TYPE_GATT_SERVICES_DISCOVERED = "Discovered services";
    public static final String TYPE_CHARACTERISTIC_READ = "Read characteristic";
    public static final String TYPE_CHARACTERISTIC_WRITE = "Write characteristic";
    public static final String TYPE_DESCRIPTOR_READ = "Read descriptor";
    public static final String TYPE_DESCRIPTOR_WRITE = "Write descriptor";
    public static final String TYPE_INDICATION_VALUE = "Indication Value";
    public static final String EXTRA_TYPE = "EXTRA_TYPE";
    public static final String EXTRA_VALUE = "EXTRA_VALUE";
    public static final String EXTRA_SERVICE_UUID = "EXTRA_SERVICE_UUID";
    public static final String EXTRA_CHARACTERISTIC_UUID = "EXTRA_CHARACTERISTIC_UUID";
    public static final String EXTRA_STATUS = "EXTRA_STATUS";
    public static final String EXTRA_ADDRESS = "EXTRA_ADDRESS";
    private static final String TAG = "BleHelperService";
    private static BleHelperService bleService;
    private final IBinder binder = new LocalBinder();
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothManager bluetoothManager;
    private BluetoothGatt bluetoothGatt;
    private Handler threadHandler = new Handler();
    private boolean isConnectedDevice;
    private boolean shouldStartConnectDevice = false;
    private boolean isScanning = false;

    private final BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            BluetoothDevice device = gatt.getDevice();

            if (status != BluetoothGatt.GATT_SUCCESS) {
                sendBroadcast(TYPE_GATT_ERROR, device, status);
            }

            Log.d(TAG, "onConnectionStateChange()" + device.getAddress() + ", "
                    + device.getName() + ", status=" + status + " newState=" + newState);
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                isConnectedDevice = true;
                Log.d(TAG, "Device Address : " + bluetoothGatt.getDevice().getAddress());
                Log.d(TAG, "Bone Status: " + bluetoothGatt.getDevice().getBondState());
                getGatt().discoverServices();
                //"Connect device"
                Log.d(TAG, "Connect Device");
                sendBroadcast(TYPE_GATT_CONNECTED, device, status);
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                isConnectedDevice = false;
                Log.d(TAG, "Disconnect Device");
                //"Disconnect device"
                getInstance().disconnectDevice();
                threadHandler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        if (!isScanning) {
                            startScan();
                        }
                    }
                }, 80L);
                sendBroadcast(TYPE_GATT_DISCONNECTED, device, status);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            BluetoothDevice device = gatt.getDevice();
            Log.d(TAG, "onServicesDiscovered()" + device.getAddress() + ", " + device.getName()
                    + ", status=" + status);
            if (shouldStartConnectDevice) {
                shouldStartConnectDevice = false;
                threadHandler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        BluetoothGatt gatt = getGatt();
                        Log.d(TAG, "TYPE_GATT_SERVICES_DISCOVERED gatt " + gatt + " service "
                                + getInstance());
                        boolean writeResult = getInstance().setIndication(gatt, true);
                        if (!writeResult) {
                            Log.d(TAG, "Write Error");
                        }
                    }
                }, 80L);
            }
            sendBroadcast(TYPE_GATT_SERVICES_DISCOVERED, device, status);
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            BluetoothDevice device = gatt.getDevice();
            Log.d(TAG, "onCharacteristicRead()" + device.getAddress() + ", " + device.getName()
                    + "characteristic=" + characteristic.getUuid().toString());
            sendBroadcast(TYPE_CHARACTERISTIC_READ, device, characteristic, status);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            BluetoothDevice device = gatt.getDevice();
            Log.d(TAG, "onCharacteristicWrite()" + device.getAddress() + ", " + device.getName()
                    + "characteristic=" + characteristic.getUuid().toString());
            sendBroadcast(TYPE_CHARACTERISTIC_WRITE, device, characteristic, status);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            BluetoothDevice device = gatt.getDevice();
            Log.d(TAG, "onCharacteristicChanged()" + device.getAddress() + ", " + device.getName()
                    + "characteristic=" + characteristic.getUuid().toString());
            parseCharcteristicValue(characteristic);
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            BluetoothDevice device = gatt.getDevice();
            Log.d(TAG, "onDescriptorRead()" + device.getAddress() + ", " + device.getName()
                    + "characteristic=" + descriptor.getCharacteristic().getUuid().toString());
            sendBroadcast(TYPE_DESCRIPTOR_READ, device, descriptor, status);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            BluetoothDevice device = gatt.getDevice();
            Log.d(TAG, "onDescriptorWrite()" + device.getAddress() + ", " + device.getName()
                    + "characteristic=" + descriptor.getCharacteristic().getUuid().toString());
            sendBroadcast(TYPE_DESCRIPTOR_WRITE, device, descriptor, status);
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            BluetoothDevice device = gatt.getDevice();
            Log.d(TAG, "onReadRemoteRssi()" + device.getAddress() + ", " + device.getName()
                    + "RSSI=" + rssi + "status=" + status);
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            BluetoothDevice device = gatt.getDevice();
            Log.d(TAG, "onReadRemoteRssi()" + device.getAddress() + ", " + device.getName()
                    + "status=" + status);
        }
    };

    public BleHelperService() {
        super();
    }

    public static BleHelperService getInstance() {
        return bleService;
    }

    public static BluetoothGatt getGatt() {
        if (bleService != null) {
            return bleService.bluetoothGatt;
        }
        return null;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        bleService = this;
        bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        if (!isScanning) {
            startScan();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isScanning) {
            stopScan();
        }
        disconnectDevice();
    }

    @Override
    public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
        Log.d(TAG, "onLeScan Found device");
        if (!isScanning) {
            Log.d(TAG, "Scanning was stopped");
            return;
        }
        if (!isAbleToConnectDevice(device) || shouldStartConnectDevice) {
            Log.d(TAG, "UnBonded Device " + isAbleToConnectDevice(device) + " " + shouldStartConnectDevice);
            return;
        }
        shouldStartConnectDevice = true;
        stopScan();
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        getInstance().connectDevice(device);
    }

    public BluetoothGattService getGattSearvice(BluetoothGatt gatt) {
        BluetoothGattService service = null;
//        Log.d(TAG, "CHK look for services");
//        for (BluetoothGattService ser : gatt.getServices()) {
//            Log.d(TAG, "CHK service:"+ser.getUuid().toString());
//        }
        for (UUID uuid : ADGattUUID.ServicesUUIDs) {
            Log.d(TAG, "CHK: Try with "+uuid);
            service = gatt.getService(uuid);
            if (service != null) {break;} else {Log.d(TAG, "CHK: was null");}
        }
        return service;
    }

    public BluetoothGattCharacteristic getGattMeasuCharacteristic(BluetoothGattService service) {
        BluetoothGattCharacteristic characteristic = null;
        for (UUID uuid : ADGattUUID.MeasuCharacUUIDs) {
            characteristic = service.getCharacteristic(uuid);
            if (characteristic != null) break;
        }
        return characteristic;
    }

    public boolean setIndication(BluetoothGatt gatt, boolean enable) {
        Log.d(TAG, "CHK setInication, get service first");
        BluetoothGattService service = getInstance().getGattSearvice(gatt);
        boolean isSuccess = false;
        if (service != null) {
            BluetoothGattCharacteristic characteristic = getInstance().getGattMeasuCharacteristic(service);
            if (characteristic != null) {
                isSuccess = gatt.setCharacteristicNotification(characteristic, enable);
                BluetoothGattDescriptor descriptor = characteristic
                        .getDescriptor(ADGattUUID.ClientCharacteristicConfiguration);
                if (enable) {
                    descriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
                } else {
                    byte[] disable = {0x00, 0x00};
                    descriptor.setValue(disable);
                }
                gatt.writeDescriptor(descriptor);
            } else {
                Log.d(TAG, "Characteristic NULL");
            }
        } else {
            Log.d(TAG, "Service NULL");
        }
        return isSuccess;
    }

    public boolean isConnectedDevice() {
        return isConnectedDevice;
    }

    public boolean connectDevice(BluetoothDevice device) {
        Log.d(TAG, "connectDevice device " + device);
        if (device == null) {
            return false;
        }
        int state = bluetoothManager.getConnectionState(device, BluetoothProfile.GATT);
        Log.w(TAG, "Attempt to connect in state: " + state);
        bluetoothGatt = device.connectGatt(this, false, bluetoothGattCallback);
        Log.d(TAG, "bluetoothGatt " + bluetoothGatt);
        return bluetoothGatt != null;
    }

    public void disconnectDevice() {
        if (bluetoothGatt == null) {
            return;
        }
        bluetoothGatt.close();
        bluetoothGatt.disconnect();
        bluetoothGatt = null;
    }

    private boolean isAbleToConnectDevice(BluetoothDevice device) {
        if (getInstance().isConnectedDevice()) {
            Log.d(TAG, "Is Connected Device");
            return false;
        }
        if (bluetoothAdapter != null) {
            Set<BluetoothDevice> pairingDevices = bluetoothAdapter.getBondedDevices();
            return pairingDevices.contains(device);
        }
        return false;
    }

    private void startScan() {
        if (getInstance().isConnectedDevice()) {
            getInstance().disconnectDevice();
        }
        isScanning = true;
        bluetoothAdapter.startLeScan(this);
    }

    private void stopScan() {
        isScanning = false;
        bluetoothAdapter.stopLeScan(this);
    }

    private void sendBroadcast(String type, BluetoothDevice device, int status) {
        Intent intent = new Intent(ACTION_BLE_SERVICE);
        intent.putExtra(EXTRA_TYPE, type);
        intent.putExtra(EXTRA_ADDRESS, device.getAddress());
        intent.putExtra(EXTRA_STATUS, status);
        sendBroadcast(intent);
    }

    private void sendBroadcast(String type, BluetoothDevice device, BluetoothGattCharacteristic ch, int status) {
        Intent intent = new Intent(ACTION_BLE_SERVICE);
        intent.putExtra(EXTRA_TYPE, type);
        intent.putExtra(EXTRA_SERVICE_UUID, ch.getService().getUuid().toString());
        intent.putExtra(EXTRA_CHARACTERISTIC_UUID, ch.getUuid().toString());
        intent.putExtra(EXTRA_VALUE, ch.getValue());
        intent.putExtra(EXTRA_STATUS, status);
        sendBroadcast(intent);
    }

    private void sendBroadcast(String type, BluetoothDevice device, BluetoothGattDescriptor descriptor, int status) {
        String serviceUuidString = descriptor.getCharacteristic().getService().getUuid().toString();
        String characteristicUuidString = descriptor.getCharacteristic().getUuid().toString();
        Intent intent = new Intent(ACTION_BLE_SERVICE);
        intent.putExtra(EXTRA_TYPE, type);
        intent.putExtra(EXTRA_SERVICE_UUID, serviceUuidString);
        intent.putExtra(EXTRA_CHARACTERISTIC_UUID, characteristicUuidString);
        intent.putExtra(EXTRA_VALUE, descriptor.getValue());
        intent.putExtra(EXTRA_STATUS, status);
        sendBroadcast(intent);
    }

    private void sendBroadcast(String type, String characteristicUuidString, Bundle bundle) {
        Intent intent = new Intent(ACTION_BLE_SERVICE);
        intent.putExtra(EXTRA_TYPE, type);
        intent.putExtra(EXTRA_CHARACTERISTIC_UUID, characteristicUuidString);
        intent.putExtra(EXTRA_VALUE, bundle);
        sendBroadcast(intent);
    }

    public void parseCharcteristicValue(BluetoothGattCharacteristic characteristic) {
        if (ADGattUUID.AndCustomWeightScaleMeasurement.equals(characteristic.getUuid())) {
            Bundle valueBundle = AndCustomWeightScaleMeasurement.readCharacteristic(characteristic);
            sendBroadcast(TYPE_INDICATION_VALUE, ADGattUUID.AndCustomWeightScaleMeasurement.toString(), valueBundle);
        } else if (ADGattUUID.BloodPressureMeasurement.equals(characteristic.getUuid())) {
            Bundle valueBundle = BloodPressureMeasurement.readCharacteristic(characteristic);
            sendBroadcast(TYPE_INDICATION_VALUE, ADGattUUID.BloodPressureMeasurement.toString(), valueBundle);
        } else if (ADGattUUID.WeightScaleMeasurement.equals(characteristic.getUuid())) {
            Bundle valueBundle = WeightMeasurement.readCharacteristic(characteristic);
            sendBroadcast(TYPE_INDICATION_VALUE, ADGattUUID.WeightScaleMeasurement.toString(), valueBundle);
        }
    }

    public class LocalBinder extends Binder {
        public BleHelperService getService() {
            return BleHelperService.this;
        }
    }
}
