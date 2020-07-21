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

import org.universAAL.middleware.owl.ManagedIndividual;

public class Dialogue extends ManagedIndividual {

    public static final String MY_URI = CoachingOntology.NAMESPACE + "Dialoge";
    // public static final String PROP_EXPIRATION = CoachingOntology.NAMESPACE + "validUntil";
    // public static final String PROP_BOUND_TO = CoachingOntology.NAMESPACE + "boundTo";

    // public static final String INSTANCE_INVALID_SESSION = SecurityOntology.NAMESPACE + "invalidSession";
    // public static ManagedIndividual invalid = new Session(INSTANCE_INVALID_SESSION);

    /**
     * Only for serializers.
     */
    public Dialogue() {
	super();
    }

    /**
     * @param uri
     */
    public Dialogue(String uri) {
	super(uri);
    }

    /** {@ inheritDoc} */
    public String getClassURI() {
	return MY_URI;
    }

    /** {@ inheritDoc} */
    public boolean isWellFormed() {
	return super.isWellFormed();
    }

    /** {@ inheritDoc} */
    public int getPropSerializationType(String propURI) {
	return PROP_SERIALIZATION_FULL;
    }

}
