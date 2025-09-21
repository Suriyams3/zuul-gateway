package com.example.zuul;

import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.ZuulRunner;


import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ZuulServlet extends HttpServlet {
    private ZuulRunner zuulRunner;

    @Override
    public void init() {
        zuulRunner = new ZuulRunner();
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        RequestContext ctx = RequestContext.getCurrentContext();
        ctx.clear();
        ctx.setRequest(req);
        ctx.setResponse(resp);

        // Set custom routing logic
        ctx.addZuulRequestHeader("x-forwarded-host", req.getHeader("Host"));

        try {
            new ZuulRoutingFilter().run(); // Your custom filter
        } catch (Exception e) {
            resp.setStatus(500);
            resp.getWriter().write("Zuul Error: " + e.getMessage());
        }
    }
}
