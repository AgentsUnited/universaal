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

import org.universAAL.middleware.service.owl.Service;

/**
 * Ontological service that controls coaching dialogs. Methods included in this
 * class are the mandatory ones for representing an ontological service in Java
 * classes for universAAL.
 *
 * @author alfiva
 */
public class CoachingService extends Service {
    public static final String MY_URI = CoachingOntology.NAMESPACE
	    + "CoachingService";
    public static final String PROP_CONTROLS = CoachingOntology.NAMESPACE
	    + "controls";

    public CoachingService(String uri) {
	super(uri);
    }

    public CoachingService() {
	super();
    }

    public String getClassURI() {
	return MY_URI;
    }

    public int getPropSerializationType(String propURI) {
	return PROP_CONTROLS.equals(propURI) ? PROP_SERIALIZATION_FULL
		: super.getPropSerializationType(propURI);
    }

    public boolean isWellFormed() {
	return true;
    }
}
