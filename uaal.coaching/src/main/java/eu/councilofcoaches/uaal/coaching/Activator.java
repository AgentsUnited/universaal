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

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.universAAL.middleware.container.ModuleContext;
import org.universAAL.middleware.container.osgi.OSGiContainer;
import org.universAAL.middleware.container.utils.LogUtils;

public class Activator implements BundleActivator {
    private static BundleContext osgiContext = null;
    private static ModuleContext context = null;
    private CoachingCallee callee = null;

    public void start(BundleContext bcontext) throws Exception {
	osgiContext = bcontext;
	context = OSGiContainer.THE_CONTAINER
		.registerModule(new Object[] { osgiContext });
	callee = new CoachingCallee(context);
    }

    public void stop(BundleContext bcontext) throws Exception {
	callee.close();
    }

    public static void logD(String method, String msg) {
	LogUtils.logDebug(context, Activator.class, method, msg);
    }

    public static void logI(String method, String msg) {
	LogUtils.logInfo(context, Activator.class, method, msg);
    }

    public static void logW(String method, String msg) {
	LogUtils.logWarn(context, Activator.class, method, msg);
    }

    public static void logE(String method, String msg) {
	LogUtils.logError(context, Activator.class, method, msg);
    }
}
