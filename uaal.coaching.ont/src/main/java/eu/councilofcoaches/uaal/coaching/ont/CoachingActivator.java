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

//import org.osgi.framework.BundleActivator;
//import org.osgi.framework.BundleContext;
import org.universAAL.middleware.container.ModuleActivator;
import org.universAAL.middleware.container.ModuleContext;
//import org.universAAL.middleware.container.osgi.OSGiContainer;
import org.universAAL.middleware.owl.OntologyManagement;

import eu.councilofcoaches.uaal.coaching.ont.model.CoachingOntology;

public class CoachingActivator implements ModuleActivator {

//    private static ModuleContext moduleContext;
    private CoachingOntology ont = new CoachingOntology();

    public void start(ModuleContext context) throws Exception {
//	moduleContext = OSGiContainer.THE_CONTAINER.registerModule(new Object[] { context });
	OntologyManagement.getInstance().register((ModuleContext) context, ont);
    }

    public void stop(ModuleContext context) throws Exception {
	OntologyManagement.getInstance().unregister((ModuleContext) context, ont);
    }
}
