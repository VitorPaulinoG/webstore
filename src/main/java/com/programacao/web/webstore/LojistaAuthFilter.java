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
import jakarta.servlet.http.HttpSession;

@WebFilter(urlPatterns = { "/lojista/*" })
public class LojistaAuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        var session = request.getSession(false);
        if (!isAuthenticated(response, session))
            return;

        if (!isAuthorized(response, session))
            return;

        chain.doFilter(request, response);
    }

    private boolean isAuthenticated(HttpServletResponse response, HttpSession session)
            throws IOException {
        if (session == null) {
            response.sendRedirect("/login");
            return false;
        }
        return true;
    }

    private boolean isAuthorized(HttpServletResponse response, HttpSession session)
            throws IOException {
        if (!session.getAttribute("user-role").equals("Lojista")) {
            response.sendRedirect("/");
            return false;
        }
        return true;
    }

}
