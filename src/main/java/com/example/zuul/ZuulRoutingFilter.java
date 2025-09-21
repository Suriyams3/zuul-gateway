package com.example.zuul;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.apache.commons.io.IOUtils;

import javax.servlet.http.HttpServletRequest;

public class ZuulRoutingFilter extends ZuulFilter {

    @Override
    public String filterType() {
        return "route";  // This filter handles routing
    }

    @Override
    public int filterOrder() {
        return 1;        // Order of execution
    }

    @Override
    public boolean shouldFilter() {
        return true;     // Always run this filter
    }

    @Override
    public Object run() {
        try {
            RequestContext ctx = RequestContext.getCurrentContext();
            HttpServletRequest request = ctx.getRequest();

            String path = request.getRequestURI();
            String targetUrl;

            if (path.startsWith("/api/service1")) {
                targetUrl = "http://localhost:8082" + path.replaceFirst("/api/service1", "");
            } else if (path.startsWith("/api/service2")) {
                targetUrl = "http://localhost:8083" + path.replaceFirst("/api/service2", "");
            } else {
                ctx.setSendZuulResponse(false);
                ctx.setResponseStatusCode(404);
                ctx.getResponse().getWriter().write("Not Found");
                return null;
            }

            // Open connection to target URL
            URL url = new URL(targetUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(request.getMethod()); // GET, POST, etc.

            // Forward headers if needed (optional - you can add code here)

            // Get response body from target
            InputStream inputStream = conn.getInputStream();
            String responseBody = IOUtils.toString(inputStream, "UTF-8");

            // Set response status and body back to client
            ctx.setResponseStatusCode(conn.getResponseCode());
            ctx.getResponse().getWriter().write(responseBody);

            // Do not send Zuul response again since we wrote directly
            ctx.setSendZuulResponse(false);
        } catch (Exception e) {
            throw new RuntimeException("Routing failed", e);
        }

        return null;
    }
}
