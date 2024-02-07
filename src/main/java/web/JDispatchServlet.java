package web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Serial;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import com.google.gson.Gson;

import datastructures.ControllersInstances;
import datastructures.ControllersMap;
import datastructures.DependencyInjectionMap;
import datastructures.RequestControllerData;
import datastructures.ServiceImplementationMap;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import util.JLogger;

public class JDispatchServlet extends HttpServlet {
    @Serial
    private static final long serialVersionUID = 1L;

    @Override
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		// preciso ignorar o favIcon
		if (request.getRequestURL().toString().endsWith("/favicon.ico")) {
			return;
		}
		PrintWriter out = new PrintWriter(response.getWriter());
		Gson gson = new Gson();

		String url = request.getRequestURI();
		String httpMethod = request.getMethod().toUpperCase();
		String key = httpMethod + url;
		RequestControllerData data = ControllersMap.values.get(key);
		JLogger.log("JDispatcherServlet", "URL:" + url + "(" + httpMethod + ") - Handler "
				+ data.getControllerClass() + "." + data.getControllerMethod());

		Object controller;
		JLogger.log("DispatcherServlet", "Searching for controller Instance");
		// verificar  instancia da classe correspondente
		try {
			controller = ControllersInstances.instances.get(data.controllerClass);
			if (controller == null) {
				JLogger.log("DispatcherServlet", "Creating new Controller Instance");
				controller = Class.forName(data.controllerClass).getDeclaredConstructor().newInstance();
				ControllersInstances.instances.put(data.controllerClass, controller);
				
				injectDependencies(controller);
			}

			// preciso extrair o método desta classe 
			Method controllerMethod = null;
			for (Method method : controller.getClass().getMethods()) {
				if (method.getName().equals(data.controllerMethod)) {
					controllerMethod = method;
					break;
				}
			}
			
			
			JLogger.log("DispatcherServlet", "Invoking method " + controllerMethod.getName() + " to handle request");

			
			// parâmetros?
			if (controllerMethod.getParameterCount() > 0) {
				JLogger.log("JDispatchServlet", "Method "+controllerMethod.getName()+ " has parameters");
				Object arg;
				Parameter parameter = controllerMethod.getParameters()[0];
				if (parameter.getAnnotations()[0].annotationType().getName().equals("annotations.JBody")) {

					String body = readBytesFromRequest(request);
					JLogger.log("","    Found Parameter from request of type "+parameter.getType().getName());
					JLogger.log("","    Parameter content: "+body);
					arg = gson.fromJson(body, parameter.getType());
					
					out.println(gson.toJson(controllerMethod.invoke(controller, arg)));
				}
			}
			else {
				out.write(gson.toJson(controllerMethod.invoke(controller)));
			}
			out.close();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	
	private String readBytesFromRequest(HttpServletRequest request) throws Exception {
		StringBuilder str = new StringBuilder();
		String line;
		BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
		while ((line = br.readLine()) != null) {
			str.append(line);
			
		}
		return str.toString();
	}
	
	private void injectDependencies(Object client) throws Exception{
		for (Field attr: client.getClass().getDeclaredFields()) {
			String attrType = attr.getType().getName();
			JLogger.log("JDispatcherServlet",  "Injected "+attr.getName() + " Field has type "+attrType);			
			Object serviceImpl;
			if (DependencyInjectionMap.objects.get(attrType) == null) {
				// declaração da interface?
				JLogger.log("DependencyInjection", "Couldn't find Instance for "+attrType);
				String implType = ServiceImplementationMap.implementations.get(attrType);
				// declaracao da implementacao
				if (implType != null) {
					JLogger.log("DependencyInjection", "Found Instance for "+implType);
					
					serviceImpl = DependencyInjectionMap.objects.get(implType);
					
					if (serviceImpl == null) {
						JLogger.log("DependencyInjection", "Injecting new object");
						serviceImpl = Class.forName(implType).getDeclaredConstructor().newInstance();
						DependencyInjectionMap.objects.put(implType, serviceImpl);
					}
					
					// atribuir instancia ao atributo
					attr.setAccessible(true);
					attr.set(client, serviceImpl);
					JLogger.log("DependencyInjection", "Injected Object successfully");
				}
			}
		}
	}
}
