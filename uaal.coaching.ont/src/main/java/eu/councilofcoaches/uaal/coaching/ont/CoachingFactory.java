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
package eu.councilofcoaches.uaal.coaching.ont;

import org.universAAL.middleware.rdf.Resource;
import org.universAAL.middleware.rdf.ResourceFactory;

import eu.councilofcoaches.uaal.coaching.ont.model.CoachingService;
import eu.councilofcoaches.uaal.coaching.ont.model.Dialogue;

public class CoachingFactory implements ResourceFactory {

    public Resource createInstance(String classURI, String instanceURI, int factoryIndex) {

	switch (factoryIndex) {
	case 0:
	    return new Dialogue(instanceURI);
	case 1:
	    return new CoachingService(instanceURI);
	}

	return null;
    }
}
