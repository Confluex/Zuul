import java.net.URL;
import java.security.ProtectionDomain;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.webapp.WebAppContext;

/**
 * Main class for running Zuul using an embedded Jetty server
 * 
 * To run this all you need to do is run the war file with 
 * java -jar <name of war file> however there are 2
 * settings that you may want to change:
 * 
 * 1. The server port this, can be configured using the system properties 'server.port'
 * e.g. -Dserver.port=9080, the default is to use 8080
 * 
 * 2. The configuration location. By default this uses a folder 
 * called 'conf' relative to the location that you started zuul, this is where it will look for the
 * ldap.properties and the zuul-data-config.properties, the system property to change this location
 * is 'conf.location'. The parameter requires a Spring resource URI 
 * e.g. -Dconf.location=file:/home/zuul/
 * 
 * 
 * @author boxheed
 *
 */
public class Main {

    /**
     * The default configuration location.
     */
    public static final String DEFAULT_CONF_LOCATION = "file:./conf/";

    /**
     * The System property for the server port
     */
    public static final String SERVER_PORT = "server.port";
    
    /**
     * The System property for the configuration location. 
     */
    public static final String CONF_LOCATION = "conf.location";

    public static void main(String[] args) throws Exception {
        setConfLocation();
        startJetty();
    }
    
    private static void startJetty() throws Exception {
        Server server = new Server();

        SelectChannelConnector connector = new SelectChannelConnector();
        connector.setPort(Integer.getInteger(SERVER_PORT, 8080));
        server.addConnector(connector);

        ProtectionDomain domain = Main.class.getProtectionDomain();
        URL location = domain.getCodeSource().getLocation();
        WebAppContext webapp = new WebAppContext();
        webapp.setContextPath("/");
        webapp.setWar(location.toExternalForm());
        server.setHandler(webapp);

        server.start();
        server.join();
    }
    
    private static void setConfLocation() {
        if(System.getProperty(CONF_LOCATION) == null) {
            System.setProperty(CONF_LOCATION, DEFAULT_CONF_LOCATION);
        }
    }
}