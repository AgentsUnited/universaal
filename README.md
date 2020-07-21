## universAAL modules

These projects are applications used to connect the Agents United platform to the universAAL IoT Platform. This allows Agents United to incorporate sensor data from devices connected in universAAL. Right now we only connect to data from weight-scales and blood pressure sensors.

* **uaal.hbaf**: This is a Java Maven project of an universAAL application that acts as a Relay App: It is run as a bundle in a Karaf OSGi instance of universAAL. It receives any data coming from weight-scales and blood pressure sensors connected to universAAL, and then delivers this data to Agents United HBAF module.
* **uaal.mobile**: This is an Android app. It connects to Continua-certified weight-scales and blood pressure sensors and provides a user interface to take their measurements. Then it sends this data to the Karaf OSGi instance of universAAL where uaal.hbaf is running.
* **uaal.coaching**: This is a Java Maven project of an universAAL application that allows other universAAL apps to start coaching sessions in Agents United. It is run as a bundle in a Karaf OSGi instance of universAAL. It publishes a universAAL service based on the coaching ontology (see below) to start coaching sessions, by sending the command to Agents United.
* **uaal.coaching.ont**: This is a Java Maven project of an universAAL ontology representing the coaching domain. It is added as a bundle in a Karaf OSGi instance of universAAL.

## Build

The **uaal.hbaf**, **uaal.coaching** and **uaal.coaching.ont** projects are Java Maven projects that produce an OSGi bundle. You need to have Maven installed and then execute `mvn install` in the folder where the `pom.xml` is.

The **uaal.mobile** project is an Android app. You can use Android sdk or the Android Studio IDE to build the `.apk`.

## Run

### Relay app
This is an universAAL bundle, you need to run it in a Karaf OSGi instance of universAAL. Then you will need to install the universAAL REST API there so that the Mobile App can send the data there.
1. Go to [universAAL's Github](https://github.com/universAAL) and clone or download the Karaf distro. The latest version, which is the one compatible with our code, is not yet released, so you will have to build the distro. Follow the instructions in https://github.com/universAAL/distro.karaf. (essentially, clone/download, then `mvn install`)
2. Before you run the Karaf distro (or afterwards, as long as you restart), go to `{your karaf folder}\etc` and edit `system.properties`.
   * Add this property `org.universAAL.ri.rest.manager.serv.host=http://192.168.1.1:9000/` and set the IP to your host's IP. This is where the universAAL REST API will be published. Make sure it's an IP the Mobile App will be able to access.
   * Add this property `eu.councilofcoaches.uaal.hbaf.url` and set it to the base URL where your Agents United HBAF server is running.
3. Run the universAAL Karaf distro by running the `{your karaf folder}\bin\karaf` script. Once you see 5 "GMS" messages in the console, it is up and running.
4. Install the universAAL REST API and the needed ontologies with the following commands in the Karaf console:
   * `feature:install uAAL-Ont.Health.Measurement`
   * `feature:install uAAL-Ont.personalhealthdevice`
   * `feature:install uAAL-Ont.Profile`
   * `feature:install uAAL-Ont.Security`
   * `feature:install uAAL-Ont.Cryptographic`
   * `feature:install uAAL-RI.RESTAPI`
5. Place the following libraries (links to their Maven repository entires below) .jar files manually in `{your karaf folder}\deploy` or through the Karaf console with the command `install mvn:{groupId}/{artifactId}/{version}`:
   * https://mvnrepository.com/artifact/commons-codec/commons-codec/1.11
   * https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-core/2.9.8
   * https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-annotations/2.9.0
   * https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-databind/2.9.8
   * https://mvnrepository.com/artifact/com.auth0/java-jwt/3.8.0
6. Place the uaal.hbaf Relay App .jar file (you will find it in its `/target` folder after you build it) and place it in the `{your karaf folder}\deploy` folder.
7. The Relay App should now be ready to relay sensor data that arrives from the Mobile App through the REST API to the Agents United HBAF. You can check that all bundles are "Active" with the Karaf command `list`.

### Mobile app
This is an Android App. You can use Android Studio or the Android sdk tools to build the .apk and then push it and install it in a mobile phone. The app requires a phone with Android 5.0 and Bluetooth Low Energy. It has been tested with A&D Medical devices UA-651 and UC-352. The devices must be paired *before* launching app. Once inside the app, follow these steps:
1. Open the Connection settings throgh the upper-right corner menu (If you are asked a developer PIN, it is 8225).
2. Find the "Remote connecion URL" setting and set it to your universAAL REST API address (The one you set up in the previous instructions, appending `/uaal` at the end).
3. Enter your credentials, which should have been created in advance and registered in HBAF.
4. Press Login, and wait until the app validates, connects and starts. If it does not connect after a while, force close the app, and start again.
5. Once the app connects you will see a button for each type of device to take the measurement. Press it, then take the measurement, and wait for it to appear in the screen. It will be immediately sent to the universAAL REST API (You should see a message pop up in the Karaf console).

### UniversAAL Agents United Coaching App integration
Coming soon.

## Troubleshooting
* If you get an error message in the app after taking a measurement about not connecting to the server, try logout (upper right menu) and login again.

## License

These modules are licensed under the GNU Lesser General Public License v3.0 (LGPL 3.0).
