/*
Copyright 2020 SABIEN - Universitat Politecnica de Valencia.
This file is part of Agents United universAAL Relay

Agents United universAAL Relay is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Agents United universAAL Relay is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Agents United universAAL Relay.  If not, see <https://www.gnu.org/licenses/>.
 */
package eu.councilofcoaches.uaal.hbaf;

public class Translator {
    public static final String TIMESTAMP = "$t";
    public static final String DIASTOLIC = "$d";
    public static final String SYSTOLIC = "$s";
    public static final String HEARTRATE = "$h";
    public static final String WEIGHT = "$w";
    public static final String DEVICE = "$i";
    
    public static final String BODY_HBAF_BPHR="{\r\n"
	+ "    \"Device_id\": \""+DEVICE+"\" ,\r\n"
    	+ "    \"Timestamp\": \""+TIMESTAMP+"\" ,\r\n"
    	+ "    \"Diastolic\": \""+DIASTOLIC+"\" ,\r\n"
    	+ "    \"Systolic\": \""+SYSTOLIC+"\" ,\r\n"
    	+ "    \"Heartrate\": \""+HEARTRATE+"\"\r\n"
    	+ "}";
    
    public static final String BODY_HBAF_BP="{\r\n"
	    	+ "    \"Device_id\": \""+DEVICE+"\" ,\r\n"
	    	+ "    \"Timestamp\": \""+TIMESTAMP+"\" ,\r\n"
	    	+ "    \"Diastolic\": \""+DIASTOLIC+"\" ,\r\n"
	    	+ "    \"Systolic\": \""+SYSTOLIC+"\"\r\n"
	    	+ "}";
    
    public static final String BODY_HBAF_HR="{\r\n"
	    	+ "    \"Device_id\": \""+DEVICE+"\" ,\r\n"
	    	+ "    \"Timestamp\": \""+TIMESTAMP+"\" ,\r\n"
	    	+ "    \"Heartrate\": \""+HEARTRATE+"\"\r\n"
	    	+ "}";
    
    public static final String BODY_HBAF_WEIGHT="{\r\n"
	    	+ "    \"Device_id\": \""+DEVICE+"\" ,\r\n"
	    	+ "    \"Timestamp\": \""+TIMESTAMP+"\" ,\r\n"
	    	+ "    \"Weight\": \""+WEIGHT+"\"\r\n"
	    	+ "}";

}
