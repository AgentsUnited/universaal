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

import java.net.InetAddress;
import java.util.Random;

public class Body {
    public static final String K_EVENTID = "EVENTID";
    public static final String K_THINGID = "THINGID";
    public static final String K_TIMESTAMP = "TIMESTAMP";
    public static final String K_BPMEASUREMENT = "BPMEASUREMENT";
    public static final String K_BPSYSVALUE = "BPSYSVALUE";
    public static final String K_BPDIASVALUE = "BPDIASVALUE";
    public static final String K_HRMEASUREMENT = "HRMEASUREMENT";
    public static final String K_HRVALUE = "HRVALUE";
    public static final String K_USERNAME = "USERNAME";
    public static final String K_ENCODED = "ENCODED";
    public static final String K_WMEASUREMENT = "WMEASUREMENT";
    public static final String K_WEIGHTUNIT = "WEIGHTUNIT";
    public static final String K_WVALUE = "WVALUE";

    public static final String UNIT_NONE = "";
    public static final String UNIT_KG = "\nns2:hasUnit <http://ontology.universAAL.org/Unit.owl#gram>;\n" +
            "  ns2:hasPrefix <http://ontology.universAAL.org/Unit.owl#kilo>;\n";
    public static final String UNIT_LB = "\nns2:hasUnit <http://ontology.universAAL.org/Unit.owl#pound>;\n";

    public static final String PREFIX_BPID = "bloodPressure";
    public static final String PREFIX_HRID = "heartRate";
    public static final String PREFIX_WID = "weight";

    public static final String DEFAULT_BPID = "bpSensor";
    public static final String DEFAULT_WID = "wSensor";
    public static final String PREFIX_SENSOR = "sensor";

    public static final String BODY_EVENT_WEIGHT = "@prefix ns: <http://ontology.universAAL.org/PhThing.owl#> . \n" +
            "@prefix ns1: <http://ontology.universAAL.org/COUCH.owl#> .\n" +
            "@prefix ns2: <http://ontology.universAAL.org/Measurement.owl#> .\n" +
            "@prefix ns3: <http://ontology.universAAL.org/HealthMeasurement.owl#> .\n" +
            "@prefix ns4: <http://ontology.universAAL.org/Device.owl#> .\n" +
            "@prefix ns5: <http://ontology.universAAL.org/PersonalHealthDevice.owl#> .\n" +
            "@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .\n" +
            "@prefix owl: <http://www.w3.org/2002/07/owl#> .\n" +
            "@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n" +
            "@prefix ns11: <http://ontology.universAAL.org/Security.owl#> .\n" +
            "@prefix ns12: <http://ontology.universAAL.org/Profile.owl#> . \n" +
            "@prefix : <http://ontology.universAAL.org/Context.owl#> .\n" +
            "<urn:org.universAAL.middleware.context.rdf:ContextEvent#_:" + K_EVENTID + ">\n" +
            "  a :ContextEvent ;\n" +
            "  <http://ontology.universAAL.org/uAAL.owl#theInvolvedHumanUser> :AP ; \n" +
            "  rdf:subject ns1:" + K_THINGID + " ;\n" +
            "  :hasTimestamp \"" + K_TIMESTAMP + "\"^^xsd:long ;\n" +
            "  rdf:predicate ns4:hasValue ;\n" +
            "  rdf:object ns1:" + K_WMEASUREMENT + " .\n" +
            "ns1:" + K_THINGID + " a ns5:WeighingScale ,\n" +
            "    ns:Device ,\n" +
            "    ns:PhysicalThing ;\n" +
            "  ns4:hasValue ns1:" + K_WMEASUREMENT + " .\n" +
            ":gauge a :ContextProviderType .\n" +
            "ns1:" + K_WMEASUREMENT + " a ns3:PersonWeight ,\n" +
            "    ns3:HealthMeasurement ,\n" +
            "    ns2:Measurement ;\n" + K_WEIGHTUNIT +
            "  ns2:hasUnit <http://ontology.universAAL.org/Unit.owl#gram>;\n" +
            "  ns2:hasPrefix <http://ontology.universAAL.org/Unit.owl#kilo>;\n" +
            "  ns2:value \"" + K_WVALUE + "\"^^xsd:float .\n" +
            ":SPP a ns11:SecuritySubprofile ,\n" +
            "    ns12:SubProfile ,\n" +
            "    ns11:Anonymizable ;\n" +
            "  ns11:associatedCredentials :UPC . \n" +
            ":APP ns12:hasSubProfile :SPP ;\n" +
            "  a ns12:AssistedPersonProfile ,\n" +
            "    ns12:UserProfile ,\n" +
            "    ns11:Anonymizable .\n " +
            ":UPC ns11:username \"" + K_USERNAME + "\"^^xsd:string ;\n" +
            "  a ns11:UserPasswordCredentials ,\n" +
            "    ns11:InherenceFactor ,\n" +
            "    ns11:Credentials ;\n" +
            "  ns11:password \"" + K_ENCODED + "\"^^xsd:base64Binary .\n" +
            ":AP ns12:hasProfile :APP ;\n" +
            "  a ns12:AssistedPerson ,\n" +
            "    ns12:User ,\n" +
            "    ns:PhysicalThing .";

    public static final String BODY_EVENT_BP_HR = "@prefix ns: <http://ontology.universAAL.org/PhThing.owl#> . \n" +
            "@prefix ns1: <http://ontology.universAAL.org/Context.owl#> . \n" +
            "@prefix ns2: <http://ontology.universAAL.org/Measurement.owl#> . \n" +
            "@prefix xsd: <http://www.w3.org/2001/XMLSchema#> . \n" +
            "@prefix owl: <http://www.w3.org/2002/07/owl#> . \n" +
            "@prefix ns3: <http://ontology.universAAL.org/HealthMeasurement.owl#> . \n" +
            "@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> . \n" +
            "@prefix ns4: <http://ontology.universAAL.org/Device.owl#> . \n" +
            "@prefix ns11: <http://ontology.universAAL.org/Security.owl#> .\n" +
            "@prefix ns12: <http://ontology.universAAL.org/Profile.owl#> . \n" +
            "@prefix : <http://ontology.universAAL.org/COUCH.owl#> . \n" +
            "<urn:org.universAAL.middleware.context.rdf:ContextEvent#_:" + K_EVENTID + "> \n" +
            "  a ns1:ContextEvent ;\n" +
            "  <http://ontology.universAAL.org/uAAL.owl#theInvolvedHumanUser> :AP ; \n" +
            "  rdf:subject :bp" + K_THINGID + " ;\n" +
            "  ns1:hasTimestamp \"" + K_TIMESTAMP + "\"^^xsd:long ;\n" +
            "  rdf:predicate ns4:hasValue ;\n" +
            "  rdf:object :" + K_BPMEASUREMENT + " .\n" +
            ":" + K_BPMEASUREMENT + "sys a ns2:Measurement ;\n" +
            "  ns2:value \"" + K_BPSYSVALUE + "\"^^xsd:float .\n" +
            "ns1:gauge a ns1:ContextProviderType .\n" +
            ":bp" + K_THINGID + " a <http://ontology.universAAL.org/PersonalHealthDevice.owl#BloodPressureSensor> ,\n" +
            "    ns:Device ,\n" +
            "    ns:PhysicalThing ;\n" +
            "  ns4:hasValue :" + K_BPMEASUREMENT + " ;\n" +
            "  ns:partOf :hr" + K_THINGID + " .\n" +
            ":" + K_BPMEASUREMENT + "dias a ns2:Measurement ;\n" +
            "  ns2:value \"" + K_BPDIASVALUE + "\"^^xsd:float .\n" +
            ":" + K_BPMEASUREMENT + " ns3:diastolicBloodPreassure :" + K_BPMEASUREMENT + "dias ;\n" +
            "  a ns3:BloodPressure ,\n" +
            "    ns3:HealthMeasurement ;\n" +
            "  ns3:systolicBloodPreassure :" + K_BPMEASUREMENT + "sys .\n" +
            ":hr" + K_THINGID + " a <http://ontology.universAAL.org/PersonalHealthDevice.owl#HeartRateSensor> ,\n" +
            "    ns:Device ,\n" +
            "    ns:PhysicalThing ;\n" +
            "  ns4:hasValue :" + K_HRMEASUREMENT + " .\n" +
            ":" + K_HRMEASUREMENT + " a ns3:HeartRate ,\n" +
            "    ns3:HealthMeasurement ,\n" +
            "    ns2:Measurement ;\n" +
            "  ns2:value \"" + K_HRVALUE + "\"^^xsd:int . \n" +
            ":SPP a ns11:SecuritySubprofile ,\n" +
            "    ns12:SubProfile ,\n" +
            "    ns11:Anonymizable ;\n" +
            "  ns11:associatedCredentials :UPC . \n" +
            ":APP ns12:hasSubProfile :SPP ;\n" +
            "  a ns12:AssistedPersonProfile ,\n" +
            "    ns12:UserProfile ,\n" +
            "    ns11:Anonymizable .\n " +
            ":UPC ns11:username \"" + K_USERNAME + "\"^^xsd:string ;\n" +
            "  a ns11:UserPasswordCredentials ,\n" +
            "    ns11:InherenceFactor ,\n" +
            "    ns11:Credentials ;\n" +
            "  ns11:password \"" + K_ENCODED + "\"^^xsd:base64Binary .\n" +
            ":AP ns12:hasProfile :APP ;\n" +
            "  a ns12:AssistedPerson ,\n" +
            "    ns12:User ,\n" +
            "    ns:PhysicalThing .";

    private static final String UUID_prefix;
    private static int counter = 0;

    static {
        StringBuilder aux = new StringBuilder("_:");
        try {
            byte[] ownIP = InetAddress.getLocalHost().getAddress();
            int val;
            for (byte b : ownIP) {
                val = b & 0xFF;
                aux.append(val < 16 ? "0" : "").append(Integer.toHexString(val));
            }
        } catch (Exception ignored) {
        }
        aux.append(Integer.toHexString(new Random(System.currentTimeMillis()).nextInt()));
        UUID_prefix = aux + ":";
    }

    public static String makeEventID() {
        return UUID_prefix + Integer.toHexString(counter++);
    }
}
