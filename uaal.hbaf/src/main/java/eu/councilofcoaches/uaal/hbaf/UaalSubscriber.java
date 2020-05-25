/*
Copyright 2020 SABIEN - Universitat Politecnica de Valencia.
This file is part of Agents United universAAL Relay

Agents United universAAL Relay is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Agents United universAAL Relay is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with Agents United universAAL Relay.  If not, see <https://www.gnu.org/licenses/>.
 */
package eu.councilofcoaches.uaal.hbaf;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.universAAL.middleware.container.ModuleContext;
import org.universAAL.middleware.context.ContextEvent;
import org.universAAL.middleware.context.ContextEventPattern;
import org.universAAL.middleware.context.ContextSubscriber;
import org.universAAL.middleware.owl.MergedRestriction;
import org.universAAL.middleware.rdf.Resource;
import org.universAAL.ontology.healthmeasurement.owl.BloodPressure;
import org.universAAL.ontology.healthmeasurement.owl.HeartRate;
import org.universAAL.ontology.healthmeasurement.owl.PersonWeight;
import org.universAAL.ontology.measurement.Measurement;
import org.universAAL.ontology.personalhealthdevice.BloodPressureSensor;
import org.universAAL.ontology.personalhealthdevice.HeartRateSensor;
import org.universAAL.ontology.personalhealthdevice.WeighingScale;
import org.universAAL.ontology.phThing.PhysicalThing;
import org.universAAL.ontology.profile.SubProfile;
import org.universAAL.ontology.profile.User;
import org.universAAL.ontology.profile.UserProfile;
import org.universAAL.ontology.security.SecuritySubprofile;
import org.universAAL.ontology.security.UserPasswordCredentials;

public class UaalSubscriber extends ContextSubscriber {
    private static final String BT_ADDRESS_DELIM = "sensor";
    private static final long TIMEDWAIT = 5000; // Millis. HR and BP events within this time are merged.
//    private Float cacheDiastolic;
//    private Float cacheSystolic;
//    private Integer cacheRate;
//    private Long cacheTIMEbp=1l;
//    private Long cacheTIMEhr=1l;
    private ConcurrentHashMap<String, CachedData> cache = new ConcurrentHashMap<String, CachedData>();

    public UaalSubscriber(ModuleContext context) {
	super(context, getContextSubscriptionParams());
    }

    public static ContextEventPattern[] getContextSubscriptionParams() {
	ContextEventPattern cep1 = new ContextEventPattern();
	cep1.addRestriction(MergedRestriction.getAllValuesRestriction(
		ContextEvent.PROP_RDF_SUBJECT, WeighingScale.MY_URI));
	ContextEventPattern cep2 = new ContextEventPattern();
	cep2.addRestriction(MergedRestriction.getAllValuesRestriction(
		ContextEvent.PROP_RDF_SUBJECT, BloodPressureSensor.MY_URI));
	ContextEventPattern cep3 = new ContextEventPattern();
	cep3.addRestriction(MergedRestriction.getAllValuesRestriction(
		ContextEvent.PROP_RDF_SUBJECT, HeartRateSensor.MY_URI));
	return new ContextEventPattern[] { cep1, cep2, cep3 };
    }

    @Override
    public void communicationChannelBroken() {
	Activator.logD("handleContextEvent", ">>>>>COMM CHANNEL BROKEN");
	System.out.println(">>>>>COMM CHANNEL BROKEN");
	cache.clear();
    }

    @Override
    public void handleContextEvent(ContextEvent event) {
	Activator.logD("handleContextEvent", ">>>>>RECEIVED EVENT");
	System.out.println(">>>>>RECEIVED EVENT");
	String typeURI = event.getSubjectTypeURI();//((Resource)event.getRDFObject()).getType();
	String btAddress = getAddress(event.getRDFSubject().getURI());
	String body = null, user = null, pass = null;
	Long t = event.getTimestamp();
	/* Use this for hardcoded auth*/
//	user=HBAFClient.LOGIN_USR;
//	pass=HBAFClient.LOGIN_PWD;
	/* Use this for per-event auth (requires using updated Context bus with Context#484 fix*/
	User u = (User) event.getInvolvedUser();
	UserProfile up = u.getUserProfile();
	SubProfile[] sps = up.getSubProfile();
	for (SubProfile sp:sps){
	    if (sp instanceof SecuritySubprofile){
		List<Resource> creds = ((SecuritySubprofile)sp).getCredentials();
		for(Resource cred:creds){
		    if (cred instanceof UserPasswordCredentials){
			user = ((UserPasswordCredentials)cred).getUsername();
			pass = new String(((UserPasswordCredentials)cred).getPassword().getVal());
			break;
		    }
		}
		break;
	    }
	}
	System.out.println(">>>>>RECEIVED EVENT TYPE: \n"+typeURI);
	try {
	    if (typeURI.equals(WeighingScale.MY_URI)) {
		PersonWeight pw = (PersonWeight) event.getRDFObject();
		Float w = (Float) pw.getProperty(Measurement.PROP_VALUE);
		body = Translator.BODY_HBAF_WEIGHT
			.replace(Translator.DEVICE, btAddress)
			.replace(Translator.TIMESTAMP, t.toString())
			.replace(Translator.WEIGHT, w.toString());
		System.out.println(">>>>>RECEIVED EVENT WEIGHT: \n"+body);
		String result=HBAFClient.postLoginAndFileSecure(HBAFClient.URL_WEIGHT_NEW, body, HBAFClient.FILE_WEIGHT, user, pass);
		System.out.println(">>>>>"+result);
	    } else if (typeURI.equals(BloodPressureSensor.MY_URI)) {
		BloodPressure bp = (BloodPressure)event.getRDFObject();
		BloodPressureSensor bps=(BloodPressureSensor)event.getRDFSubject();
		Object hrs = bps.getProperty(PhysicalThing.PROP_PART_OF);
		if (hrs!=null && hrs instanceof HeartRateSensor){
		    System.out.println(">>>>>RECEIVED EVENT BP: Includes HR");
		    HeartRate hr = ((HeartRateSensor) hrs).getValue();
		    sendBPHR(btAddress, 
			    ((Float) bp.getDias().getValue()).toString(), 
			    ((Float) bp.getSyst().getValue()).toString(), 
			    ((Integer) hr.getProperty(Measurement.PROP_VALUE)).toString(), 
			    user, pass);
		}else{
		    CachedData data = CachedData.from(cache.get(user));
		    data.cacheDiastolic = (Float) bp.getDias().getValue();
		    data.cacheSystolic = (Float) bp.getSyst().getValue();
		    data.cacheTIMEbp = t;
		    cache.put(user, data);
		    System.out.println(">>>>>RECEIVED EVENT BP: Checking if needs to be merged with HR");
		    sendMergeBPHR(btAddress, user, pass);
		}
	    } else if (typeURI.equals(HeartRateSensor.MY_URI)) {
		HeartRate hr = (HeartRate) event.getRDFObject();
		CachedData data = CachedData.from(cache.get(user));
		data.cacheRate = (Integer) hr.getProperty(Measurement.PROP_VALUE);
		data.cacheTIMEhr = t;
		cache.put(user, data);
		System.out.println(">>>>>RECEIVED EVENT HR: Checking if needs to be merged with BP");
		sendMergeBPHR(btAddress, user, pass);
	    } else {
		Activator.logW("handleContextEvent", ">>>>>UNSOLICITED EVENT");
		return;
	    }
	} catch (Exception e) {
	    Activator.logE("handleContextEvent", ">>>>>ERROR SENDING TO HBAF: "+e);
	    e.printStackTrace();
	}
    }

    private static String getAddress(String uri) {
	if (uri!=null){
	    int i = uri.indexOf(BT_ADDRESS_DELIM);
	    if (i > -1) return uri.substring(i+6);
	}
	return uri;
    }

    private void sendMergeBPHR(String btAddress, String user, String pass) throws Exception{
	CachedData data = CachedData.from(cache.get(user));
	if (Math.abs(data.cacheTIMEbp-data.cacheTIMEhr)>TIMEDWAIT){
	    // If HR and BP timestamps are not within X sec, they are not the same event.
	    // If HR arrives first, do nothing, just wait for upcoming BP to check this. And vice-versa.
	} else {
	    // If HR and BP timestamps are within X sec, they are the same event, merge into the same JSON body
	    sendBPHR(btAddress, data.cacheDiastolic.toString(), data.cacheSystolic.toString(), data.cacheRate.toString(), user, pass);
	}
    }
    
    private void sendBPHR(String btAddress, String dias, String sys, String rate, String user, String pass) throws Exception{
	String body = Translator.BODY_HBAF_BPHR
		.replace(Translator.DEVICE, btAddress)
		.replace(Translator.TIMESTAMP, Long.toString(System.currentTimeMillis()))
		.replace(Translator.DIASTOLIC, dias)
		.replace(Translator.SYSTOLIC, sys)
		.replace(Translator.HEARTRATE, rate);
	System.out.println(">>>>>COMBINED EVENT HR and BP: \n"+body);
	String result = HBAFClient.postLoginAndFileSecure(HBAFClient.URL_BP_NEW, body, HBAFClient.FILE_BP, user, pass);
	System.out.println(">>>>>"+result);
    }

    @Override
    public void close() {
	cache.clear();
	super.close();
    }
    
    
}