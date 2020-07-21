/*
Copyright 2020 SABIEN - Universitat Politecnica de Valencia.
This file is part of Agents United universAAL Ontology

Agents United universAAL Ontology is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Agents United universAAL Ontology is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with Agents United universAAL Ontology.  If not, see <https://www.gnu.org/licenses/>.
 */
package eu.councilofcoaches.uaal.coaching.ont.model;

import org.universAAL.middleware.owl.DataRepOntology;
import org.universAAL.middleware.owl.ManagedIndividual;
import org.universAAL.middleware.owl.MergedRestriction;
import org.universAAL.middleware.owl.OntClassInfoSetup;
import org.universAAL.middleware.owl.Ontology;
import org.universAAL.middleware.rdf.Resource;
import org.universAAL.middleware.service.owl.Service;

import eu.councilofcoaches.uaal.coaching.ont.CoachingFactory;

/**
 * @author universAAL Studio: UML2Java transformation
 */
public final class CoachingOntology extends Ontology {

    private static CoachingFactory factory = new CoachingFactory();
    public static final String NAMESPACE = "http://councilofcoaches.eu/Coaching.owl#";

    public CoachingOntology() {
	super(NAMESPACE);
    }

    public void create() {
	Resource r = getInfo();
	r.setResourceComment("The Agents United Coaching Ontology");
	r.setResourceLabel("coaching");
	addImport(DataRepOntology.NAMESPACE);

	// ******* Declaration of enumeration classes of the ontology ******* //

	// OntClassInfoSetup oci_ValueType =
	// createNewAbstractOntClassInfo(ValueType.MY_URI);

	// ******* Declaration of regular classes of the ontology ******* //
	OntClassInfoSetup oci_Dialogue = createNewOntClassInfo(Dialogue.MY_URI,	factory, 0);
	OntClassInfoSetup oci_Service = createNewOntClassInfo(CoachingService.MY_URI, factory, 1);

	// ******* Add content to enumeration classes of the ontology ******* //

	// oci_TemperatureLevelValue.setResourceComment("");
	// oci_TemperatureLevelValue.setResourceLabel("TemperatureLevelValue");
	// oci_TemperatureLevelValue.toEnumeration(new ManagedIndividual[] {
	// TemperatureLevelValue.HighTemperature,
	// TemperatureLevelValue.LowTemperature,
	// TemperatureLevelValue.ChangeTooFast });

	// ******* Add content to regular classes of the ontology ******* //

	oci_Dialogue.setResourceComment("A Coaching Dialogue");
	oci_Dialogue.setResourceLabel("Dialogue");
	oci_Dialogue.addSuperClass(ManagedIndividual.MY_URI);
	
	
	oci_Service = createNewOntClassInfo(CoachingService.MY_URI, factory, 1);
	oci_Service.setResourceComment("The class of services managing coaching dialogues");
	oci_Service.setResourceLabel("Coaching Services");
	oci_Service.addSuperClass(Service.MY_URI);

	oci_Service.addObjectProperty(CoachingService.PROP_CONTROLS);
	oci_Service.addRestriction(
		MergedRestriction.getAllValuesRestriction(CoachingService.PROP_CONTROLS, Dialogue.MY_URI));
    }
}
