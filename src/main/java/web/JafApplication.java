package web;

import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;

import java.io.File;

public class JafApplication {
    public static void run() {
        try {

            Tomcat tomcat = new Tomcat();
            Connector connector = new Connector();

            connector.setPort(8080);
            tomcat.setConnector(connector);

            Context context = tomcat.addContext("", new File(".").getAbsolutePath());
            Tomcat.addServlet(context, "JafDispatchServlet", new JafDispatchServlet());

            context.addServletMappingDecoded("/*", "JafDispatchServlet");

            tomcat.start();
            tomcat.getServer().await();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
