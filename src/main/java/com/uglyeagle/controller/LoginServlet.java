package com.uglyeagle.controller;

import com.uglyeagle.jass.SimpleCallbackHandler;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        char[] password = request.getParameter("password").toCharArray();

        try {
            LoginContext context
                    = new LoginContext("FileLogin", new SimpleCallbackHandler(username, new String(password)));

            context.login(); // authenticate

            HttpSession session = request.getSession(true);
            session.setAttribute("username", username);

            boolean isAdmin = context
                    .getSubject()
                    .getPrincipals()
                    .stream()
                    .anyMatch(p -> p.getName().equals("ADMIN"));

            if (isAdmin) {
                response.getWriter().println("Welcome Admin: " + username);
            } else {
                response.getWriter().println("Welcome User: " + username);
            }

        } catch (LoginException le) {
            // le.printStackTrace();
            System.err.println("error occured");
            response.getWriter().println("Login failed: " + le.getMessage());
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }

}
