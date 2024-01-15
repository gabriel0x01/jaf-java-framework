package web;

import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import util.JLogger;

import java.io.File;

public class JApplication {
    public static void run(Class<?> sourceClass) {

        // zera o log do apache tomcat
        java.util.logging.Logger.getLogger("org.apache").setLevel(java.util.logging.Level.OFF);
        long ini, fim;
        JLogger.showBanner();

        try {
            ini = System.currentTimeMillis();

            JLogger.log("Embeded Web Container", "Starting (...)"  + sourceClass.getSimpleName());

            Tomcat tomcat = new Tomcat();
            JLogger.log("Embeded Web Container", "Web Container started on port 8080");
            Connector connector = new Connector();

            connector.setPort(8080);
            tomcat.setConnector(connector);

            Context context = tomcat.addContext("", new File(".").getAbsolutePath());
            Tomcat.addServlet(context, "JafDispatchServlet", new JDispatchServlet());

            context.addServletMappingDecoded("/*", "JafDispatchServlet");

            tomcat.start();
            fim = System.currentTimeMillis();
            JLogger.log("Embeded Web Container", "started in " + ((double) (fim - ini) / 1000) + " seconds");
            tomcat.getServer().await();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
