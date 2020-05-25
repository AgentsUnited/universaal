package jp.co.aandd.bleSimpleApp.gatt;

import java.util.Calendar;
import java.util.Locale;

import jp.co.aandd.bleSimpleApp.base.ADGattService;
import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Bundle;
import android.util.Log;

public class AndCustomWeightScaleMeasurement extends ADGattService {

	private static final String TAG = "CustomMeasurement";

	public static Bundle readCharacteristic(BluetoothGattCharacteristic characteristic) {
		Bundle bundle = new Bundle();
		int flag = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
		String flagString = Integer.toBinaryString(flag);
		int offset=0;
		for(int index = flagString.length(); 0 < index ; index--) {
			String key = flagString.substring(index-1 , index);
			
			if(index == flagString.length()) {
				double convertValue = 0;
				if(key.equals("0")) {
					bundle.putString(KEY_UNIT, WeightMeasurement.VALUE_WEIGHT_SCALE_UNITS_KG);
					convertValue = 0.1f;
				}
				else {
					bundle.putString(KEY_UNIT, WeightMeasurement.VALUE_WEIGHT_SCALE_UNITS_LBS);
					convertValue = 0.1f;
				}
				// Unit
				offset+=1;
				
				// Value
				double value = (double)(characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, offset)) * convertValue;
				Log.d(TAG, "V :"+value);
				bundle.putDouble(KEY_WEIGHT, value);
				offset+=2;
			}
			else if(index == flagString.length()-1) {
				if(key.equals("1")) {
					
					Log.d(TAG, "Y :"+String.format("%04d", characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, offset)));
					bundle.putInt(KEY_YEAR, characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, offset));
					offset+=2;
					Log.d(TAG, "M :"+String.format("%02d", characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset)));
					bundle.putInt(KEY_MONTH, characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset));
					offset+=1;
					Log.d(TAG, "D :"+String.format("%02d", characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset)));
					bundle.putInt(KEY_DAY, characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset));
					offset+=1;
					
					Log.d(TAG, "H :"+String.format("%02d", characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset)));
					bundle.putInt(KEY_HOURS, characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset));
					offset+=1;
					Log.d(TAG, "M :"+String.format("%02d", characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset)));
					bundle.putInt(KEY_MINUTES, characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset));
					offset+=1;
					Log.d(TAG, "S :"+String.format("%02d", characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset)));
					bundle.putInt(KEY_SECONDS, characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset));
					offset+=1;
				}
				else {
					Calendar calendar = Calendar.getInstance(Locale.getDefault());
					bundle.putInt(KEY_YEAR, calendar.get(Calendar.YEAR));
					bundle.putInt(KEY_MONTH, calendar.get(Calendar.MONTH)+1);
					bundle.putInt(KEY_DAY, calendar.get(Calendar.DAY_OF_MONTH));
					bundle.putInt(KEY_HOURS, calendar.get(Calendar.HOUR));
					bundle.putInt(KEY_MINUTES, calendar.get(Calendar.MINUTE));
					bundle.putInt(KEY_SECONDS, calendar.get(Calendar.SECOND));
				}
			}
			else if(index == flagString.length()-2) {
				if(key.equals("1")) {
					Log.d(TAG, "ID :"+String.format("%02d", characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset)));
					offset+=1;
				}
			}
			else if(index == flagString.length()-3) {
				if(key.equals("1")) {
					// BMI and Height
				}
			}
		}
		return bundle;
	}
}
