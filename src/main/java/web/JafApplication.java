package web;

import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import util.JafLogger;

import java.io.File;

public class JafApplication {
    public static void run() {

        // zera o log do apache tomcat
        java.util.logging.Logger.getLogger("org.apache").setLevel(java.util.logging.Level.OFF);
        long ini, fim;
        JafLogger.showBanner();

        try {
            ini = System.currentTimeMillis();
            JafLogger.log("Embeded Web Container", "Starting (...)");

            Tomcat tomcat = new Tomcat();
            Connector connector = new Connector();

            connector.setPort(8080);
            tomcat.setConnector(connector);

            Context context = tomcat.addContext("", new File(".").getAbsolutePath());
            Tomcat.addServlet(context, "JafDispatchServlet", new JafDispatchServlet());

            context.addServletMappingDecoded("/*", "JafDispatchServlet");

            tomcat.start();
            fim = System.currentTimeMillis();
            JafLogger.log("Embeded Web Container", "started in " + ((double) (fim - ini) / 1000) + " seconds");
            tomcat.getServer().await();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
