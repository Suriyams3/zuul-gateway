package com.example.zuul;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class Main {
    public static void main(String[] args) throws Exception {
        Server server = new Server(8079);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        // Add Zuul Servlet
        context.addServlet(new ServletHolder(new ZuulServlet()), "/*");

        server.start();
        System.out.println("API Gateway started at http://localhost:8079");
        server.join();
    }
}
