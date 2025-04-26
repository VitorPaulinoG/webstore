package com.programacao.web.webstore;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebFilter(urlPatterns = { "/produto", "/produtos" })
public class ClientAuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        var session = request.getSession(false);
        if (session == null) {
            response.sendRedirect("/login");
            return;
        }

        if (!session.getAttribute("user-role").equals("Cliente")) {
            response.sendRedirect("/");
            return;
        }

        chain.doFilter(request, response);
    }
}
