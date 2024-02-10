package web;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;

import annotations.JGetMethod;
import annotations.JPostMethod;
import datastructures.ControllersMap;
import datastructures.RequestControllerData;
import datastructures.ServiceImplementationMap;
import discover.ClassDiscover;
import util.JLogger;

public class JApplication {
	public static void run(Class<?> sourceClass) {

		// zera o log do apache tomcat
		java.util.logging.Logger.getLogger("org.apache").setLevel(java.util.logging.Level.OFF);
		
		long ini, fim;
		
		JLogger.showBanner();

		try {
			ini = System.currentTimeMillis();

			JLogger.log("Embeded Web Container", "Starting... " + sourceClass.getSimpleName());

			extractMetaData(sourceClass);

			Tomcat tomcat = new Tomcat(); // Permitir iniciar o embed tomcat 
			JLogger.log("Embeded Web Container", "Web Container started on port 8080");
			
			Connector connector = new Connector();
			connector.setPort(8080);
			tomcat.setConnector(connector);

			Context context = tomcat.addContext("", new File(".").getAbsolutePath()); // Onde tomcat vai procurar classes (pacote da aplicação)
			Tomcat.addServlet(context, "JDispatchServlet", new JDispatchServlet());

			context.addServletMappingDecoded("/*", "JDispatchServlet"); // Servlet Dispatch será executado em qualquer requisição

			tomcat.start();
			
			fim = System.currentTimeMillis();
			
			JLogger.log("Embeded Web Container", "started in " + ((double) (fim - ini) / 1000) + " seconds");
			
			tomcat.getServer().await();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
    // Anotações elementos descritivos que guiarão as ações do servlet

	private static void extractMetaData(Class<?> sourceClass) throws Exception {

		List<String> allClasses = ClassDiscover.retrieveAllClasses(sourceClass);
		for (String jClass : allClasses) {
			// anotações da classe
			Annotation annotations[] = Class.forName(jClass).getAnnotations();
			for (Annotation classAnnotation : annotations) {
				if (classAnnotation.annotationType().getName().equals("annotations.JController")) {
					JLogger.log("Metadata Explorer", "Found a Controller " + jClass);
					extractMethod(jClass);
				}
				else if (classAnnotation.annotationType().getName().equals("annotations.JService")) {
					JLogger.log("Metadata Explorer", "Found a Service Implementation "+jClass);					
					
					for (Class<?> interf: Class.forName(jClass).getInterfaces()) {
						JLogger.log("Metadata Explorer","    Class implements "+ interf.getName());
						ServiceImplementationMap.implementations.put(interf.getName(), jClass);						
					}
				}
			}

		}

		// varrendo estrutura de dados 
		for (RequestControllerData item : ControllersMap.values.values()) {
			JLogger.log("", "    " + item.httpMethod + ":" + item.url + " [" + item.controllerClass + "."
					+ item.controllerMethod + "]");
		}

	}

	private static void extractMethod(String className) throws Exception {
		// metodos da classe
		String httpMethod = "";
		String path = "";
		for (Method method : Class.forName(className).getDeclaredMethods()) {
			// anotacoes do metodo
			for (Annotation annotation : method.getAnnotations()) {
				if (annotation.annotationType().getName().equals("annotations.JGetMethod")) {
					path = ((JGetMethod) annotation).value();
					// JLogger.log("", " + method:" + method.getName() + " - URL GET = " + path);
					httpMethod = "GET";

				} else if (annotation.annotationType().getName()
						.equals("annotations.JPostMethod")) {
					path = ((JPostMethod) annotation).value();
					// JLogger.log("", " + method:" + method.getName() + " - URL POST=" + path);

					httpMethod = "POST";
				}
				// Isere as informacoes na estrutura de dados, para que o dispatch servlet consulte posteriormente
				RequestControllerData getData = new RequestControllerData(httpMethod, path, className,
						method.getName());
				ControllersMap.values.put(httpMethod + path, getData);
			}
		}
	}
}
