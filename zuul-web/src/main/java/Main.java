import java.net.URL;
import java.security.ProtectionDomain;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.webapp.WebAppContext;

public class Main {

    
    public static void main(String[] args) throws Exception {
        setConfLocation();
        startJetty();
    }
    
    private static void startJetty() throws Exception {
        Server server = new Server();

        SelectChannelConnector connector = new SelectChannelConnector();
        connector.setPort(Integer.getInteger("server.port", 8080));
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
        if(System.getProperty("conf.location") == null) {
            System.setProperty("conf.location", "file:./conf/");
        }
    }
}