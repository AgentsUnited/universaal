/*
Copyright 2020 SABIEN - Universitat Politecnica de Valencia.
This file is part of Agents United universAAL app

Agents United universAAL app is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Agents United universAAL app is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with Agents United universAAL app.  If not, see <https://www.gnu.org/licenses/>.
 */
package eu.councilofcoaches.uaal.coaching;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.universAAL.middleware.container.ModuleContext;
import org.universAAL.middleware.owl.OntologyManagement;
import org.universAAL.middleware.service.CallStatus;
import org.universAAL.middleware.service.ServiceCall;
import org.universAAL.middleware.service.ServiceCallee;
import org.universAAL.middleware.service.ServiceResponse;
import org.universAAL.middleware.service.owl.Service;
import org.universAAL.middleware.service.owls.process.ProcessOutput;
import org.universAAL.middleware.service.owls.profile.ServiceProfile;

import eu.councilofcoaches.uaal.coaching.ont.model.CoachingOntology;
import eu.councilofcoaches.uaal.coaching.ont.model.CoachingService;
import eu.councilofcoaches.uaal.coaching.ont.model.Dialogue;

public class CoachingCallee extends ServiceCallee {
    private static final String SERVICE_START_DIALOGUE = "servStartDialogue";
    private static final String OUT_GET_VALUE = "outStartedDialogue";

    protected CoachingCallee(ModuleContext context) {
	super(context, new ServiceProfile[] { getProfileStartDialogue() });
    }

    public static ServiceProfile getProfileStartDialogue() {
	Service startDialogue = (Service) OntologyManagement.getInstance()
		.getResource(CoachingService.MY_URI,
			CoachingOntology.NAMESPACE + SERVICE_START_DIALOGUE);
	startDialogue.addOutput(CoachingOntology.NAMESPACE + OUT_GET_VALUE,
		Dialogue.MY_URI, 1, 1,
		new String[] { CoachingService.PROP_CONTROLS });
	return startDialogue.getProfile();
    }

    @Override
    public ServiceResponse handleCall(ServiceCall call) {
	Activator.logD("handleCall", "Received service call");
	if (call == null) {
	    Activator.logW("handleCall", "Corrupt call");
	    ServiceResponse fail = new ServiceResponse(CallStatus.serviceSpecificFailure);
	    fail.addOutput(new ProcessOutput(ServiceResponse.PROP_SERVICE_SPECIFIC_ERROR, "Corrupt call"));
	    return fail;
	}
	String operation = call.getProcessURI();
	if (operation == null) {
	    Activator.logW("handleCall", "Corrupt operation");
	    ServiceResponse fail = new ServiceResponse(CallStatus.serviceSpecificFailure);
	    fail.addOutput(new ProcessOutput(ServiceResponse.PROP_SERVICE_SPECIFIC_ERROR, "Corrupt operation"));
	    return fail;
	} else if (operation.startsWith(CoachingOntology.NAMESPACE + SERVICE_START_DIALOGUE)) {

	    Activator.logD("handleCall", "Received service call to start dialogue");
	    try {
		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(); // TODO Broker URL
		Connection connection = connectionFactory.createConnection();
		connection.start();
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		Destination destination = session.createTopic("/SESSIONCONTROLLER/");
		MessageProducer producer = session.createProducer(destination);
		producer.setTimeToLive(3000);
		producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

		String text = "{\"startSession\":true}";
		TextMessage message = session.createTextMessage(text);
		Activator.logE("ActiveMQMessageProducer", "Sent message: " + message.hashCode()); // TODO Remove
		producer.send(message);

		session.close();
		connection.close();
	    } catch (Exception e) {
		Activator.logE("ActiveMQMessageProducer", "Error sending command to Agents: " + e);
		e.printStackTrace();
	    }

	    // TODO Return Dialogue representation?
	    ServiceResponse response = new ServiceResponse(CallStatus.succeeded);
	    response.addOutput(new ProcessOutput(CoachingOntology.NAMESPACE + OUT_GET_VALUE, new Dialogue()));
	    return response;
	}
	Activator.logW("handleCall", "Invalid call");
	ServiceResponse fail = new ServiceResponse(CallStatus.serviceSpecificFailure);
	fail.addOutput(new ProcessOutput(ServiceResponse.PROP_SERVICE_SPECIFIC_ERROR, "Invalid call"));
	return fail;
    }

    @Override
    public void communicationChannelBroken() {
	// TODO Auto-generated method stub
	Activator.logD("communicationChannelBroken", "Service bus closed");
    }

}
